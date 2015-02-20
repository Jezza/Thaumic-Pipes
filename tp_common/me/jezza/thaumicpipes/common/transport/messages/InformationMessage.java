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

public class InformationMessage extends NetworkMessageAbstract {

    private Aspect aspect;
    private int amount;

    public InformationMessage(INetworkNode owner, Aspect aspect, int amount) {
        super(owner);
        this.aspect = aspect;
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
        return MessageResponse.VALID;
    }

    @Override
    public MessageResponse processNode(IMessageProcessor messageProcessor, INetworkNode node) {
        IThaumicPipe pipe = (IThaumicPipe) node;
        ArmStateHandler armStateHandler = pipe.getArmStateHandler();

        Collection<IEssentiaTransport> outputs = armStateHandler.getOutputs();

        return MessageResponse.VALID;
    }

    @Override
    public MessageResponse postProcessing(IMessageProcessor messageProcessor) {
//            getOwner().notifyNode(0, amount, aspect);
//            return MessageCompletion.DELETE;

        return MessageResponse.WAIT;
    }
}
