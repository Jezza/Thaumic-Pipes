package me.jezza.thaumicpipes.common.grid.interfaces;

import me.jezza.thaumicpipes.common.core.utils.CoordSet;
import me.jezza.thaumicpipes.common.grid.TransmitType;
import me.jezza.thaumicpipes.common.multipart.pipe.thaumic.ThaumicPipePart;

/**
 * Used to transport various objects around the network.
 */
public interface INetworkMessage {

    /**
     * Check if the message should move to this coordSet next.
     */
    public boolean shouldMoveTo(CoordSet coordSet);

    /**
     * Moves the message to the coordSet passed in.
     */
    public void moveTo(INetworkHandler handler, CoordSet coordSet);

    /**
     * Can the handler dispose of the message
     */
    public boolean shouldDisposeOf(ThaumicPipePart part, CoordSet coordSet);

    /**
     * Called on disposal;
     */
    public void onDisposal(ThaumicPipePart part, CoordSet coordSet);

    /**
     * Called as many times as the message stays in the pipe.
     * 
     * Should probably create a timeout with it.
     */
    public void process(ThaumicPipePart part, CoordSet coordSet);

    /**
     * Used to determine the transmission method.
     */
    public TransmitType getTransmitType();

}
