package me.jezza.thaumicpipes.common.transport.messages;

import me.jezza.oc.api.network.NetworkMessageAbstract;
import me.jezza.oc.api.network.interfaces.IMessageProcessor;
import me.jezza.oc.api.network.interfaces.INetworkNode;
import me.jezza.oc.common.utils.CoordSet;
import me.jezza.thaumicpipes.common.core.interfaces.IThaumicPipe;
import me.jezza.thaumicpipes.common.transport.connection.ArmStateHandler;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.aspects.IEssentiaTransport;

import java.util.*;

import static me.jezza.oc.api.network.NetworkResponse.MessageResponse;
import static me.jezza.thaumicpipes.common.transport.Wrappers.*;

/**
 * Used to grab information about the current network.
 * Such as outputs and storage devices.
 * <p/>
 * This information is then used to send out AspectMessages to fill up the target tile.
 * This is solely for input devices.
 */
public class InformationMessage extends NetworkMessageAbstract {

    private Map<Aspect, AspectWrapper> aspectMap = new LinkedHashMap<>();
    private List<AspectWrapper> fallback = new ArrayList<>();

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

    private void processTiles(INetworkNode node, Collection<IEssentiaTransport> connections, boolean output) {
        for (IEssentiaTransport transport : connections) {
            Aspect type = transport.getSuctionType(null);
            if (type != null) {
                if (!aspects.aspects.containsKey(type))
                    continue;
                if (!aspectMap.containsKey(type)) {
                    AspectWrapper wrapper = new AspectWrapper(node, transport);
                    wrapper.output = output;
                    aspectMap.put(type, wrapper);
                } else {
                    AspectWrapper prevWrapper = aspectMap.get(type);
                    if (!output && prevWrapper.output)
                        continue;

                    AspectWrapper wrapper = new AspectWrapper(node, transport);
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
                fallback.add(new AspectWrapper(node, transport));
        }
    }

    @Override
    public MessageResponse postProcessing(IMessageProcessor messageProcessor) {
        if (aspectMap.isEmpty() && fallback.isEmpty())
            return MessageResponse.VALID;

        Iterator<AspectWrapper> iterator = fallback.iterator();

        for (Aspect aspect : aspects.getAspects()) {
            if (aspect == null)
                continue;
            AspectWrapper wrapper;

            if (aspectMap.containsKey(aspect))
                wrapper = aspectMap.get(aspect);
            else {
                if (iterator.hasNext()) {
                    wrapper = iterator.next();
                } else
                    continue;
            }

            int amount = Math.min(depositAmount, aspects.getAmount(aspect));
            AspectListWrapper input = new AspectListWrapper(aspects, (CoordSet) getOwner().notifyNode(0, 0));
            messageProcessor.postMessage(new AspectMessage(getOwner(), wrapper.node, input, new TransportWrapper(wrapper.transport), aspect, amount));
        }
        return MessageResponse.VALID;
    }
}
