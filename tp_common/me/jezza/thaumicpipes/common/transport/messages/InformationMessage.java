package me.jezza.thaumicpipes.common.transport.messages;

import me.jezza.oc.api.network.NetworkMessageAbstract;
import me.jezza.oc.api.network.interfaces.IMessageProcessor;
import me.jezza.thaumicpipes.common.core.interfaces.IThaumicPipe;
import me.jezza.thaumicpipes.common.transport.connection.ArmStateHandler;
import me.jezza.thaumicpipes.common.transport.wrappers.AspectListWrapper;
import me.jezza.thaumicpipes.common.transport.wrappers.EssentiaTransportWrapper;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;

import java.util.*;

import static me.jezza.oc.api.network.NetworkResponse.MessageResponse;

/**
 * Used to grab information about the current network.
 * Such as outputs and storage devices.
 * <p/>
 * This information is then used to send out AspectMessages to fill up the target tile.
 * This is solely for input devices.
 */
public class InformationMessage extends NetworkMessageAbstract<IThaumicPipe> {

    private Map<Aspect, TreeSet<EssentiaTransportWrapper>> aspectMap = new LinkedHashMap<>();
    private List<EssentiaTransportWrapper> fallback = new ArrayList<>();

    private final AspectList aspects;
    private final int depositAmount;

    public InformationMessage(IThaumicPipe owner, AspectList aspects, int depositAmount) {
        super(owner);
        this.aspects = aspects;
        this.depositAmount = depositAmount;
    }

    @Override
    public void resetMessage() {
    }

    @Override
    public void onDataChanged(IThaumicPipe node) {
    }

    @Override
    public MessageResponse preProcessing(IMessageProcessor<IThaumicPipe> messageProcessor) {
        return MessageResponse.VALID;
    }

    @Override
    public MessageResponse processNode(IMessageProcessor<IThaumicPipe> messageProcessor, IThaumicPipe pipe) {
        ArmStateHandler armStateHandler = pipe.getArmStateHandler();

        processTiles(pipe, armStateHandler.getOutputs(), true);
        processTiles(pipe, armStateHandler.getStorage(), false);

        return MessageResponse.VALID;
    }

    private void processTiles(IThaumicPipe pipe, List<EssentiaTransportWrapper> connections, boolean output) {
        for (EssentiaTransportWrapper transport : connections) {
            if (transport.shouldSkip())
                continue;

            Aspect type = transport.getSuctionType();
            if (type != null) {
                if (!aspects.aspects.containsKey(type))
                    continue;
                if (!aspectMap.containsKey(type))
                    aspectMap.put(type, new TreeSet<EssentiaTransportWrapper>());
                aspectMap.get(type).add(transport);
            } else
                fallback.add(transport);

        }
    }

    @Override
    public MessageResponse postProcessing(IMessageProcessor<IThaumicPipe> messageProcessor) {
        AspectListWrapper wrapper = new AspectListWrapper(aspects, getOwner().getCoordSet());
        Iterator<EssentiaTransportWrapper> iterator = fallback.iterator();

        for (Aspect aspect : aspects.getAspects()) {
            if (aspect == null)
                continue;
            int amount = Math.min(depositAmount, aspects.getAmount(aspect));
            if (amount <= 0)
                continue;

            if (aspectMap.containsKey(aspect)) {
                ArrayDeque<EssentiaTransportWrapper> deque = new ArrayDeque<>(aspectMap.get(aspect));
                messageProcessor.postMessage(new AspectMessage(getOwner(), wrapper, deque, aspect, amount));
            } else if (iterator.hasNext())
                messageProcessor.postMessage(new AspectMessage(getOwner(), wrapper, iterator.next(), aspect, amount));
        }
        return MessageResponse.VALID;
    }
}
