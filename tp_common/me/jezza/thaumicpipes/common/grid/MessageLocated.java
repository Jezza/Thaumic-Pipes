package me.jezza.thaumicpipes.common.grid;

import java.util.Iterator;
import java.util.LinkedList;

import me.jezza.thaumicpipes.common.core.TPLogger;
import me.jezza.thaumicpipes.common.core.utils.CoordSet;
import me.jezza.thaumicpipes.common.grid.interfaces.INetworkHandler;
import me.jezza.thaumicpipes.common.grid.interfaces.INetworkMessage;
import me.jezza.thaumicpipes.common.multipart.pipe.thaumic.ThaumicPipePart;

public abstract class MessageLocated implements INetworkMessage {

    private LinkedList<CoordSet> mapTo;
    private final CoordSet destination;

    public MessageLocated(CoordSet destination) {
        this.destination = destination;
        mapTo = new LinkedList<CoordSet>();
    }

    public void setMap(LinkedList<CoordSet> mapTo) {
        this.mapTo = mapTo;
    }

    public void setMapFromInverse(LinkedList<CoordSet> mapTo) {
        LinkedList<CoordSet> temp = new LinkedList<CoordSet>();
        Iterator<CoordSet> itr = mapTo.descendingIterator();

        while (itr.hasNext())
            temp.add(itr.next());

        this.mapTo = temp;
    }

    public CoordSet getNextSet() {
        return mapTo.isEmpty() ? null : mapTo.get(0);
    }

    @Override
    public boolean shouldMoveTo(CoordSet coordSet) {
        return mapTo.isEmpty() ? false : getNextSet().equals(coordSet);
    }

    @Override
    public void moveTo(INetworkHandler handler, CoordSet coordSet) {
        if (shouldMoveTo(coordSet))
            mapTo.removeFirst();
        else
            mapTo.addFirst(coordSet);
    }

    public boolean hasArrived(CoordSet coordSet) {
        return coordSet.equals(destination);
    }

    @Override
    public boolean shouldDisposeOf(ThaumicPipePart part, CoordSet coordSet) {
        return false;
    }

    @Override
    public void process(ThaumicPipePart part, CoordSet coordSet) {
        if (hasArrived(coordSet))
            TPLogger.info("HALP");
    }

    @Override
    public TransmitType getTransmitType() {
        return TransmitType.DIRECTIONAL;
    }

}
