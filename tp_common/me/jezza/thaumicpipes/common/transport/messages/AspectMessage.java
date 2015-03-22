package me.jezza.thaumicpipes.common.transport.messages;

import me.jezza.oc.api.network.NetworkMessageAbstract;
import me.jezza.oc.api.network.interfaces.IMessageProcessor;
import me.jezza.oc.api.network.interfaces.ISearchResult;
import me.jezza.oc.common.utils.CoordSet;
import me.jezza.thaumicpipes.ThaumicPipes;
import me.jezza.thaumicpipes.common.core.interfaces.IEssentiaWrapper;
import me.jezza.thaumicpipes.common.core.interfaces.IThaumicPipe;
import me.jezza.thaumicpipes.common.transport.wrappers.EssentiaTransportWrapper;
import thaumcraft.api.aspects.Aspect;

import java.util.ArrayList;
import java.util.Deque;
import java.util.List;

import static me.jezza.oc.api.network.NetworkResponse.MessageResponse;

/**
 * This class is slightly more simple than the other message classes.
 * This class revolves around adding the actual essentia to the target tiles.
 * <p/>
 * This also handles the finding of the path, and the execution of the aspect trail particle effect. <TBI>
 */
public class AspectMessage extends NetworkMessageAbstract<IThaumicPipe> {

    private final IEssentiaWrapper input;
    private Deque<EssentiaTransportWrapper> targets;
    private EssentiaTransportWrapper currentTarget;
    private final Aspect aspect;
    private int amount;

    private ISearchResult<IThaumicPipe> searchResult;
    private List<IThaumicPipe> path;

    public AspectMessage(IThaumicPipe owner, IEssentiaWrapper input, EssentiaTransportWrapper target, Aspect aspect, int amount) {
        super(owner);
        this.input = input;
        currentTarget = target;
        this.aspect = aspect;
        if (amount < 1)
            throw new IllegalArgumentException("Amount must be a positive integer");
        this.amount = amount;
    }

    public AspectMessage(IThaumicPipe owner, IEssentiaWrapper input, Deque<EssentiaTransportWrapper> targets, Aspect aspect, int amount) {
        super(owner);
        this.input = input;
        this.targets = targets;
        currentTarget = targets.removeFirst();
        this.aspect = aspect;
        if (amount < 1)
            throw new IllegalArgumentException("Amount must be a positive integer");
        this.amount = amount;
    }

    @Override
    public void resetMessage() {
        currentTarget = targets.removeFirst();
    }

    @Override
    public void onDataChanged(IThaumicPipe node) {
    }

    @Override
    public MessageResponse preProcessing(IMessageProcessor<IThaumicPipe> messageProcessor) {
        if (searchResult == null)
            searchResult = messageProcessor.getPathFrom(getOwner(), currentTarget.pipe);

        if (path == null) {
            if (searchResult.hasFinished())
                path = searchResult.getPath();
            else
                return MessageResponse.WAIT;
        }

        return path == null ? MessageResponse.INVALID : MessageResponse.VALID;
    }

    @Override
    public MessageResponse processNode(IMessageProcessor<IThaumicPipe> messageProcessor, IThaumicPipe node) {
        return MessageResponse.INVALID;
    }

    @Override
    public MessageResponse postProcessing(IMessageProcessor<IThaumicPipe> messageProcessor) {
        int amountAdded = currentTarget.add(aspect, amount);
        if (amountAdded <= 0)
            return targets == null ? MessageResponse.VALID : !targets.isEmpty() ? MessageResponse.INVALID : MessageResponse.VALID;
        input.remove(aspect, amountAdded);

        List<CoordSet> worldPath = new ArrayList<>();
        worldPath.add(input.getCoordSet());
        worldPath.add(getOwner().getCoordSet());

        if (!path.isEmpty())
            for (IThaumicPipe pipe : path)
                worldPath.add(pipe.getCoordSet());

        worldPath.add(currentTarget.getCoordSet());

        if (getOwner().world() != null)
            ThaumicPipes.proxy.spawnAspectTrail(getOwner().world().provider.dimensionId, worldPath, aspect);

        return MessageResponse.VALID;
    }
}
