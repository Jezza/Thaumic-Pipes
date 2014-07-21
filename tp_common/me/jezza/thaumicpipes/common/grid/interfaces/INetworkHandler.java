package me.jezza.thaumicpipes.common.grid.interfaces;

import me.jezza.thaumicpipes.common.multipart.pipe.thaumic.ThaumicPipePart;

public interface INetworkHandler {

    public void sendMessage(INetworkMessage message);

    public void receiveMessage(INetworkMessage message);

    public void processMessages(ThaumicPipePart part);

    public boolean dispose(INetworkMessage message);

}
