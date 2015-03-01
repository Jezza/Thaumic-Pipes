package me.jezza.thaumicpipes.common.transport.messages;

import me.jezza.oc.api.network.NetworkMessageAbstract;
import me.jezza.oc.api.network.interfaces.IMessageProcessor;
import me.jezza.oc.api.network.interfaces.INetworkNode;
import me.jezza.thaumicpipes.common.core.interfaces.IThaumicPipe;
import me.jezza.thaumicpipes.common.transport.connection.ArmStateHandler;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.IEssentiaTransport;

import java.util.Collection;

import static me.jezza.oc.api.network.NetworkResponse.MessageResponse;
import static me.jezza.thaumicpipes.common.transport.Wrappers.AspectWrapper;
import static me.jezza.thaumicpipes.common.transport.Wrappers.TransportWrapper;

public class StorageMessage extends NetworkMessageAbstract {

    private AspectWrapper storageTarget, outputTarget;

    private final IEssentiaTransport storage;
    private final int currentAmount;
    private final Aspect aspect;
    private final int suction;

    public StorageMessage(INetworkNode owner, IEssentiaTransport storage, Aspect aspect) {
        super(owner);
        this.aspect = aspect;
        this.storage = storage;
        this.suction = storage.getMinimumSuction();
        this.currentAmount = storage.getEssentiaAmount(null);
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

        processOutputs(node, armStateHandler.getOutputs());
        processStorage(node, armStateHandler.getStorage());

        return MessageResponse.VALID;
    }

    private void processOutputs(INetworkNode node, Collection<IEssentiaTransport> connections) {
        for (IEssentiaTransport transport : connections) {
            int suctionAmount = transport.getSuctionAmount(null);

            if (transport.getSuctionType(null) == aspect) {
                if (outputTarget == null)
                    outputTarget = new AspectWrapper(node, transport);
                else if (suctionAmount > outputTarget.transport.getSuctionAmount(null))
                    outputTarget = new AspectWrapper(node, transport);
            }
        }
    }

    private void processStorage(INetworkNode node, Collection<IEssentiaTransport> connections) {
        for (IEssentiaTransport transport : connections) {
            if (transport.equals(storage))
                continue;

            if (transport.getSuctionType(null) != aspect)
                continue;

            int suctionAmount = transport.getSuctionAmount(null);
            if (suctionAmount < transport.getMinimumSuction())
                continue;

            if (suctionAmount > suction) {
                if (storageTarget == null)
                    storageTarget = new AspectWrapper(node, transport);
                else if (suctionAmount > storageTarget.transport.getSuctionAmount(null))
                    storageTarget = new AspectWrapper(node, transport);
            } else if (suctionAmount == suction && transport.getEssentiaAmount(null) + 1 < currentAmount) {
                if (storageTarget == null)
                    storageTarget = new AspectWrapper(node, transport);
                else if (suctionAmount > storageTarget.transport.getSuctionAmount(null))
                    storageTarget = new AspectWrapper(node, transport);
            }

        }
    }

    @Override
    public MessageResponse postProcessing(IMessageProcessor messageProcessor) {
        int amount = storage.getEssentiaAmount(null);
        if (amount < 1)
            return MessageResponse.VALID;

        int amountToRemove = 1;
        if (outputTarget != null) {
            messageProcessor.postMessage(new AspectMessage(getOwner(), outputTarget.node, new TransportWrapper(storage), outputTarget.toWrapper(), aspect, amountToRemove));
            return MessageResponse.VALID;
        }

        if (storageTarget != null)
            messageProcessor.postMessage(new AspectMessage(getOwner(), storageTarget.node, new TransportWrapper(storage), storageTarget.toWrapper(), aspect, amountToRemove));

        return MessageResponse.VALID;
    }

}
