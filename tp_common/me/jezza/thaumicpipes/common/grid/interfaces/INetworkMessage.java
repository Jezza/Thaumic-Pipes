package me.jezza.thaumicpipes.common.grid.interfaces;

import me.jezza.thaumicpipes.common.core.utils.CoordSet;
import me.jezza.thaumicpipes.common.multipart.pipe.thaumic.ThaumicPipePart;

/**
 * Used to transport various objects around the network.
 */
public interface INetworkMessage {

    /**
     * Called on disposal, not reference specific, as it's called from INetworkHandler
     */
    public void onDisposal(INetworkHandler handler, ThaumicPipePart part, CoordSet coordSet);

    /**
     * Called each tick as long as the message stays in a pipe.
     * Should probably create a timeout with it.
     */
    public void process(INetworkHandler handler, ThaumicPipePart part, CoordSet coordSet);

}
