package me.jezza.thaumicpipes.common.grid;

import java.util.LinkedList;

import me.jezza.thaumicpipes.common.core.utils.CoordSet;
import me.jezza.thaumicpipes.common.grid.interfaces.IMessageOrigin;
import me.jezza.thaumicpipes.common.grid.interfaces.INetworkHandler;
import me.jezza.thaumicpipes.common.grid.interfaces.INetworkMessage;

public abstract class MessageLocating implements INetworkMessage {

    private IMessageOrigin origin;
    private LinkedList<CoordSet> visitedSet;

    public MessageLocating(IMessageOrigin origin) {
        this.origin = origin;
        this.visitedSet = new LinkedList<CoordSet>();
    }

    public void setOrigin(IMessageOrigin origin) {
        this.origin = origin;
    }

    @Override
    public boolean shouldMoveTo(CoordSet coordSet) {
        return !visitedSet.contains(coordSet);
    }

    @Override
    public void moveTo(INetworkHandler handler, CoordSet coordSet) {
        visitedSet.add(coordSet);
    }

    public LinkedList<CoordSet> getVisitedSet() {
        return visitedSet;
    }

    /**
     * To be used as a resend.
     */
    public void flushVisitedSet() {
        visitedSet.clear();
    }

    public IMessageOrigin getOrigin() {
        return origin;
    }

    @Override
    public TransmitType getTransmitType() {
        return TransmitType.OMIDIRECTIONAL;
    }
}
