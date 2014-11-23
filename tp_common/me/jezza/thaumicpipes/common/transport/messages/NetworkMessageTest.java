package me.jezza.thaumicpipes.common.transport.messages;

import me.jezza.oc.api.NetworkResponse.MessageResponse;
import me.jezza.oc.api.abstracts.NetworkMessageAbstract;
import me.jezza.oc.api.interfaces.IMessageProcessor;
import me.jezza.oc.api.interfaces.INetworkNode;
import me.jezza.thaumicpipes.common.lib.CoreProperties;

import java.util.LinkedList;
import java.util.List;

public class NetworkMessageTest extends NetworkMessageAbstract {

    private List<INetworkNode> nodes;

    public NetworkMessageTest(INetworkNode owner) {
        super(owner);
        nodes = new LinkedList<>();
    }

    @Override
    public void resetMessage() {
        nodes.clear();
    }

    @Override
    public MessageResponse isValidNode(INetworkNode node) {
//        if (!(node instanceof IThaumicPipe))
//            return MessageResponse.VALID;

//        IThaumicPipe pipe = (IThaumicPipe) node;
        nodes.add(node);
        return MessageResponse.VALID;
    }

    @Override
    public MessageResponse onMessageComplete(IMessageProcessor messageProcessor) {
        CoreProperties.logger.info("Fired: " + nodes.size());
        return MessageResponse.VALID;
    }
}
