package me.jezza.thaumicpipes.common.transport.messages;

import me.jezza.oc.api.network.NetworkMessageAbstract;
import me.jezza.oc.api.network.interfaces.IMessageProcessor;
import me.jezza.oc.api.network.interfaces.INetworkNode;
import me.jezza.oc.api.network.interfaces.ISearchResult;
import me.jezza.oc.common.utils.CoordSet;
import me.jezza.thaumicpipes.ThaumicPipes;
import me.jezza.thaumicpipes.common.core.interfaces.IEssentiaWrapper;
import net.minecraft.world.World;
import thaumcraft.api.aspects.Aspect;

import java.util.ArrayList;
import java.util.List;

import static me.jezza.oc.api.network.NetworkResponse.MessageResponse;

/**
 * This class is slightly more simple than the other message classes.
 * This class revolves around adding the actual essentia to the target tiles.
 * <p/>
 * This also handles the finding of the path, and the execution of the aspect trail particle effect. <TBI>
 */
public class AspectMessage extends NetworkMessageAbstract {

    private final INetworkNode target;
    private final IEssentiaWrapper input, output;
    private final Aspect aspect;
    private int amount;

    private ISearchResult searchResult;
    private List<INetworkNode> path;

    public AspectMessage(INetworkNode owner, INetworkNode target, IEssentiaWrapper input, IEssentiaWrapper output, Aspect aspect, int amount) {
        super(owner);
        this.input = input;
        this.output = output;
        this.target = target;
        this.aspect = aspect;
        if (amount < 1)
            throw new IllegalArgumentException("Amount must be a positive integer");
        this.amount = amount;
    }

    @Override
    public void resetMessage() {
    }

    @Override
    public void onDataChanged(INetworkNode node) {
    }

    @Override
    public MessageResponse preProcessing(IMessageProcessor messageProcessor) {
        if (searchResult == null)
            searchResult = messageProcessor.getPathFrom(getOwner(), target);

        if (path == null) {
            if (searchResult.hasFinished())
                path = searchResult.getPath();
            else
                return MessageResponse.WAIT;
        }

        return MessageResponse.VALID;
    }

    @Override
    public MessageResponse processNode(IMessageProcessor messageProcessor, INetworkNode node) {
        return MessageResponse.INVALID;
    }

    @Override
    public MessageResponse postProcessing(IMessageProcessor messageProcessor) {
        if (path == null)
            return MessageResponse.VALID;

        List<CoordSet> worldPath = new ArrayList<>();
        worldPath.add(input.getCoordSet());
        CoordSet coordSet = (CoordSet) getOwner().notifyNode(0, 0);
        if (coordSet == null)
            return MessageResponse.VALID;
        worldPath.add(coordSet);

        if (!path.isEmpty())
            for (INetworkNode node : path) {
                coordSet = (CoordSet) node.notifyNode(0, 0);
                if (coordSet == null)
                    return MessageResponse.VALID;
                worldPath.add(coordSet);
            }
        worldPath.add(output.getCoordSet());

        World world = (World) getOwner().notifyNode(1, 0);
        ThaumicPipes.proxy.spawnAspectTrail(world.provider.dimensionId, worldPath, aspect);

        int amountAdded = output.add(aspect, amount);
        if (amountAdded <= 0)
            return MessageResponse.VALID;
        input.remove(aspect, amountAdded);

        return MessageResponse.VALID;
    }
}
