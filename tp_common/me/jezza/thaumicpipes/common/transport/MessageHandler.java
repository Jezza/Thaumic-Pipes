package me.jezza.thaumicpipes.common.transport;

import me.jezza.thaumicpipes.common.core.TPLogger;
import me.jezza.thaumicpipes.common.core.utils.CoordSet;
import me.jezza.thaumicpipes.common.grid.MessageLocating;
import me.jezza.thaumicpipes.common.grid.MessagePing;
import me.jezza.thaumicpipes.common.grid.interfaces.IMessageOrigin;
import me.jezza.thaumicpipes.common.grid.interfaces.INetworkHandler;
import me.jezza.thaumicpipes.common.grid.interfaces.INetworkMessage;
import me.jezza.thaumicpipes.common.multipart.pipe.thaumic.ThaumicPipePart;

import java.util.LinkedList;

public class MessageHandler implements IMessageOrigin, INetworkHandler {

    private LinkedList<INetworkMessage> messageList;
    private LinkedList<INetworkMessage> disposeList;

    public MessageHandler() {
        messageList = new LinkedList<INetworkMessage>();
        disposeList = new LinkedList<INetworkMessage>();
    }

    public void sendPing(CoordSet coordSet) {
        sendMessage(new MessagePing(this, coordSet));
    }

    @Override
    public void sendMessage(INetworkMessage message) {
        messageList.add(message);
    }

    @Override
    public void receiveMessage(INetworkMessage message) {
        messageList.add(message);
    }

    @Override
    public void processMessages(ThaumicPipePart part, CoordSet coordSet) {
        if (messageList.isEmpty())
            return;

        if (!disposeList.isEmpty()) {
            messageList.removeAll(disposeList);
            for (INetworkMessage message : disposeList)
                message.onDisposal(this, part, coordSet);
            disposeList.clear();
        }

        for (int i = 0; i < messageList.size(); i++) {
            INetworkMessage message = messageList.get(i);
            if (message != null)
                message.process(this, part, coordSet);
        }
    }

    @Override
    public boolean dispose(INetworkMessage message) {
        return disposeList.add(message);
    }

    @Override
    public void onPacketCompletion(MessageLocating message) {
        TPLogger.info(message.getVisitedSet());
    }
}
