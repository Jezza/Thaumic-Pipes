package me.jezza.thaumicpipes.common.transport.messages;

import me.jezza.oc.api.network.NetworkMessageAbstract;
import me.jezza.oc.api.network.interfaces.IMessageProcessor;
import me.jezza.oc.api.network.interfaces.INetworkNode;
import me.jezza.oc.common.utils.CoordSet;
import me.jezza.thaumicpipes.common.core.interfaces.IThaumicPipe;
import me.jezza.thaumicpipes.common.transport.connection.ArmStateHandler;
import me.jezza.thaumicpipes.common.transport.wrappers.AspectListWrapper;
import me.jezza.thaumicpipes.common.transport.wrappers.EssentiaWrapper;
import me.jezza.thaumicpipes.common.transport.wrappers.TransportWrapper;
import net.minecraftforge.common.util.ForgeDirection;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.aspects.IEssentiaTransport;

import java.util.*;

import static me.jezza.oc.api.network.NetworkResponse.MessageResponse;

/**
 * Used to grab information about the current network.
 * Such as outputs and storage devices.
 * <p/>
 * This information is then used to send out AspectMessages to fill up the target tile.
 * This is solely for input devices.
 */
public class InformationMessage extends NetworkMessageAbstract {

    private Map<Aspect, TransportWrapper> aspectMap = new LinkedHashMap<>();
    private List<TransportWrapper> fallback = new ArrayList<>();

    private final AspectList aspects;
    private final int depositAmount;

    public InformationMessage(INetworkNode owner, AspectList aspects, int depositAmount) {
        super(owner);
        this.aspects = aspects;
        this.depositAmount = depositAmount;
    }

    @Override
    public void resetMessage() {
    }

    @Override
    public void onDataChanged(INetworkNode node) {
    }

    @Override
    public MessageResponse preProcessing(IMessageProcessor messageProcessor) {
        return MessageResponse.VALID;
    }

    @Override
    public MessageResponse processNode(IMessageProcessor messageProcessor, INetworkNode node) {
        IThaumicPipe pipe = (IThaumicPipe) node;
        ArmStateHandler armStateHandler = pipe.getArmStateHandler();

        processTiles(node, armStateHandler.getOutputs(), true);
        processTiles(node, armStateHandler.getStorage(), false);

        return MessageResponse.VALID;
    }

    private void processTiles(INetworkNode node, Map<ForgeDirection, IEssentiaTransport> connections, boolean output) {

        for (Map.Entry<ForgeDirection, IEssentiaTransport> entry : connections.entrySet()) {
            IEssentiaTransport transport = entry.getValue();
            ForgeDirection direction = entry.getKey();

            Aspect type = transport.getSuctionType(direction);
            if (type != null) {
                if (!aspects.aspects.containsKey(type))
                    continue;
                if (!aspectMap.containsKey(type)) {
                    TransportWrapper wrapper = new TransportWrapper(node, transport, direction);
                    wrapper.output = output;
                    aspectMap.put(type, wrapper);
                } else {
                    TransportWrapper prevWrapper = aspectMap.get(type);
                    if (!output && prevWrapper.output)
                        continue;

                    TransportWrapper wrapper = new TransportWrapper(node, transport, direction);
                    if (output && !prevWrapper.output) {
                        wrapper.output = true;
                        aspectMap.put(type, wrapper);
                        continue;
                    }

                    int prevSuctionAmount = prevWrapper.transport.getMinimumSuction();
                    int newSuctionAmount = wrapper.transport.getMinimumSuction();

                    if (newSuctionAmount > prevSuctionAmount)
                        aspectMap.put(type, wrapper);
                }
            } else
                fallback.add(new TransportWrapper(node, transport, direction));

        }
    }

    @Override
    public MessageResponse postProcessing(IMessageProcessor messageProcessor) {
        if (aspectMap.isEmpty() && fallback.isEmpty())
            return MessageResponse.VALID;

        Iterator<TransportWrapper> iterator = fallback.iterator();

        for (Aspect aspect : aspects.getAspects()) {
            if (aspect == null)
                continue;
            TransportWrapper wrapper;

            if (aspectMap.containsKey(aspect))
                wrapper = aspectMap.get(aspect);
            else {
                if (iterator.hasNext()) {
                    wrapper = iterator.next();
                } else
                    continue;
            }

            int amount = Math.min(depositAmount, aspects.getAmount(aspect));
            CoordSet coordSet = (CoordSet) getOwner().notifyNode(0, 0);

            if (coordSet == null)
                return MessageResponse.VALID;

            AspectListWrapper input = new AspectListWrapper(aspects, coordSet);
            messageProcessor.postMessage(new AspectMessage(getOwner(), wrapper.node, input, new EssentiaWrapper(wrapper.transport, wrapper.direction), aspect, amount));
        }
        return MessageResponse.VALID;
    }
}
