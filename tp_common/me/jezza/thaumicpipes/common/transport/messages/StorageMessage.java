package me.jezza.thaumicpipes.common.transport.messages;

import me.jezza.oc.api.network.NetworkMessageAbstract;
import me.jezza.oc.api.network.interfaces.IMessageProcessor;
import me.jezza.thaumicpipes.common.core.interfaces.IThaumicPipe;
import me.jezza.thaumicpipes.common.transport.connection.ArmStateHandler;
import me.jezza.thaumicpipes.common.transport.wrappers.EssentiaTransportWrapper;
import thaumcraft.api.aspects.Aspect;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.TreeSet;

import static me.jezza.oc.api.network.NetworkResponse.MessageResponse;

public class StorageMessage extends NetworkMessageAbstract<IThaumicPipe> {

    private TreeSet<EssentiaTransportWrapper> wrappers;

    private final EssentiaTransportWrapper input;
    private final int currentAmount;
    private final Aspect aspect;
    private final int suction;

    public StorageMessage(IThaumicPipe owner, EssentiaTransportWrapper input, Aspect aspect) {
        super(owner);
        this.aspect = aspect;
        this.input = input;
        this.suction = input.getMinimumSuction();
        this.currentAmount = input.getEssentiaAmount();
        wrappers = new TreeSet<>();
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


        for (EssentiaTransportWrapper transport : armStateHandler.getOutputs())
            if (!transport.shouldSkip()) {
                Aspect type = transport.getSuctionType();
                if (type == null || type == aspect)
                    wrappers.add(transport);
            }

        for (EssentiaTransportWrapper transport : armStateHandler.getStorage())
            if (!transport.shouldSkip() && !input.equals(transport)) {
                int suctionAmount = transport.getSuctionAmount();
                if (transport.getSuctionType() == aspect && (suctionAmount > suction || suctionAmount == suction && transport.getEssentiaAmount() + 1 < currentAmount))
                    wrappers.add(transport);
            }

        return MessageResponse.VALID;
    }

    @Override
    public MessageResponse postProcessing(IMessageProcessor<IThaumicPipe> messageProcessor) {
        if (wrappers.isEmpty())
            return MessageResponse.VALID;
        Deque<EssentiaTransportWrapper> deque = new ArrayDeque<>(wrappers);
        messageProcessor.postMessage(new AspectMessage(getOwner(), input, deque, aspect, 1));
        return MessageResponse.VALID;
    }
}
