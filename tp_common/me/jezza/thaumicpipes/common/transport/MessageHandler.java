package me.jezza.thaumicpipes.common.transport;

import java.util.LinkedList;
import java.util.List;

import me.jezza.thaumicpipes.api.interfaces.IThaumicPipe;
import me.jezza.thaumicpipes.common.core.TPLogger;
import me.jezza.thaumicpipes.common.core.utils.CoordSet;
import me.jezza.thaumicpipes.common.grid.MessageLocating;
import me.jezza.thaumicpipes.common.grid.MessagePing;
import me.jezza.thaumicpipes.common.grid.interfaces.IMessageOrigin;
import me.jezza.thaumicpipes.common.grid.interfaces.INetworkHandler;
import me.jezza.thaumicpipes.common.grid.interfaces.INetworkMessage;
import me.jezza.thaumicpipes.common.multipart.pipe.thaumic.ThaumicPipePart;
import me.jezza.thaumicpipes.common.transport.connection.ArmState;

public class MessageHandler implements IMessageOrigin, INetworkHandler {

    private LinkedList<INetworkMessage> disposeList;
    private LinkedList<INetworkMessage> messageList;

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
    public void processMessages(ThaumicPipePart part) {
        if (messageList.isEmpty())
            return;

        CoordSet partSet = part.getCoordSet();
        if (!disposeList.isEmpty()) {
            messageList.removeAll(disposeList);
            for (INetworkMessage message : disposeList)
                message.onDisposal(part, partSet);
            disposeList.clear();
        }

        List<ArmState> armList = part.getArmStateHandler().getPipeConnections();
        for (int i = 0; i < messageList.size(); i++) {
            INetworkMessage message = messageList.get(i);
            message.process(part, partSet);

            if (message.shouldDisposeOf(part, partSet)) {
                dispose(message);
                continue;
            }

            if (armList.isEmpty())
                continue;

            switch (message.getTransmitType()) {
                case DIRECTIONAL:
                    handleDirectional(message, armList);
                    break;
                case OMIDIRECTIONAL:
                    handleOmnidirectional(message, armList);
                    break;
                default:
                    break;
            }
        }
    }

    private void handleDirectional(INetworkMessage message, List<ArmState> armList) {
        for (ArmState armState : armList) {
            CoordSet coordSet = armState.getCoordSet();
            if (message.shouldMoveTo(coordSet)) {
                message.moveTo(this, coordSet);
                ((IThaumicPipe) armState.getTileEntity()).getPipe().getNetworkHandler().receiveMessage(message);
                dispose(message);
                break;
            }
        }
    }

    private void handleOmnidirectional(INetworkMessage message, List<ArmState> armList) {
        for (ArmState armState : armList) {
            CoordSet coordSet = armState.getCoordSet();
            if (message.shouldMoveTo(coordSet)) {
                message.moveTo(this, coordSet);
                ((IThaumicPipe) armState.getTileEntity()).getPipe().getNetworkHandler().receiveMessage(message);
            }
        }
        dispose(message);
    }

    @Override
    public boolean dispose(INetworkMessage message) {
        return disposeList.add(message);
    }

    @Override
    public void onPacketCompletion(MessageLocating message) {
        TPLogger.info("HALPPPPPPPPPP");
        TPLogger.info(message.getVisitedSet());
    }
}
