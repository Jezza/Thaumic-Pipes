package me.jezza.thaumicpipes.common.transport.messages;

import me.jezza.oc.api.network.NetworkMessageAbstract;
import me.jezza.oc.api.network.interfaces.IMessageProcessor;
import me.jezza.oc.api.network.interfaces.INetworkNode;
import me.jezza.thaumicpipes.common.core.interfaces.IThaumicPipe;
import me.jezza.thaumicpipes.common.transport.connection.ArmStateHandler;
import me.jezza.thaumicpipes.common.transport.wrappers.EssentiaWrapper;
import me.jezza.thaumicpipes.common.transport.wrappers.TransportWrapper;
import net.minecraftforge.common.util.ForgeDirection;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.IEssentiaTransport;

import java.util.Map;

import static me.jezza.oc.api.network.NetworkResponse.MessageResponse;

public class StorageMessage extends NetworkMessageAbstract {

    private TransportWrapper storageTarget, outputTarget;

    private final IEssentiaTransport storage;
    private final int currentAmount;
    private final Aspect aspect;
    private final int suction;
    private final ForgeDirection direction;

    public StorageMessage(INetworkNode owner, IEssentiaTransport storage, ForgeDirection direction, Aspect aspect) {
        super(owner);
        this.aspect = aspect;
        this.storage = storage;
        this.suction = storage.getMinimumSuction();
        this.currentAmount = storage.getEssentiaAmount(null);
        this.direction = direction;
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

    private void processOutputs(INetworkNode node, Map<ForgeDirection, IEssentiaTransport> connections) {
        for (Map.Entry<ForgeDirection, IEssentiaTransport> entry : connections.entrySet()) {
            IEssentiaTransport transport = entry.getValue();
            ForgeDirection direction = entry.getKey();
            int suctionAmount = transport.getSuctionAmount(direction);
            Aspect type = transport.getSuctionType(direction);
            if (type == aspect) {
                if (outputTarget == null)
                    outputTarget = new TransportWrapper(node, transport, direction);
                else if (suctionAmount > outputTarget.transport.getSuctionAmount(direction))
                    outputTarget = new TransportWrapper(node, transport, direction);
            } else if (type == null && outputTarget == null)
                outputTarget = new TransportWrapper(node, transport, direction);
        }
    }

    private void processStorage(INetworkNode node, Map<ForgeDirection, IEssentiaTransport> connections) {
        for (Map.Entry<ForgeDirection, IEssentiaTransport> entry : connections.entrySet()) {
            IEssentiaTransport transport = entry.getValue();
            ForgeDirection direction = entry.getKey();
            if (transport.equals(storage))
                continue;

            if (transport.getSuctionType(direction) != aspect)
                continue;

            int suctionAmount = transport.getSuctionAmount(direction);
            if (suctionAmount < transport.getMinimumSuction())
                continue;

            if (suctionAmount > suction) {
                if (storageTarget == null)
                    storageTarget = new TransportWrapper(node, transport, direction);
                else if (suctionAmount > storageTarget.transport.getSuctionAmount(null))
                    storageTarget = new TransportWrapper(node, transport, direction);
            } else if (suctionAmount == suction && transport.getEssentiaAmount(null) + 1 < currentAmount) {
                if (storageTarget == null)
                    storageTarget = new TransportWrapper(node, transport, direction);
                else if (suctionAmount > storageTarget.transport.getSuctionAmount(null))
                    storageTarget = new TransportWrapper(node, transport, direction);
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
            messageProcessor.postMessage(new AspectMessage(getOwner(), outputTarget.node, new EssentiaWrapper(storage, direction), outputTarget.toWrapper(), aspect, amountToRemove));
            return MessageResponse.VALID;
        }

        if (storageTarget != null)
            messageProcessor.postMessage(new AspectMessage(getOwner(), storageTarget.node, new EssentiaWrapper(storage, direction), storageTarget.toWrapper(), aspect, amountToRemove));

        return MessageResponse.VALID;
    }

}
