package me.jezza.thaumicpipes.common.transport.messages;

import me.jezza.oc.api.network.NetworkMessageAbstract;
import me.jezza.oc.api.network.interfaces.IMessageProcessor;
import me.jezza.oc.api.network.interfaces.INetworkNode;

import static me.jezza.oc.api.network.NetworkResponse.MessageResponse;

public class AspectMessage extends NetworkMessageAbstract {
    public AspectMessage(INetworkNode owner) {
        super(owner);
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
        return MessageResponse.VALID;
    }

    @Override
    public MessageResponse postProcessing(IMessageProcessor messageProcessor) {
        return MessageResponse.VALID;
    }
}
