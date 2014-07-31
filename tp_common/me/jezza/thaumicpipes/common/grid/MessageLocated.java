package me.jezza.thaumicpipes.common.grid;

import me.jezza.thaumicpipes.common.core.utils.CoordSet;
import me.jezza.thaumicpipes.common.grid.interfaces.INetworkHandler;
import me.jezza.thaumicpipes.common.grid.interfaces.INetworkMessage;
import me.jezza.thaumicpipes.common.multipart.pipe.thaumic.ThaumicPipePart;
import me.jezza.thaumicpipes.common.transport.connection.ArmState;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

public abstract class MessageLocated implements INetworkMessage {

    private final CoordSet destination;
    private LinkedList<CoordSet> mapTo;

    public MessageLocated(CoordSet destination, LinkedList<CoordSet> mapTo) {
        this.destination = destination;
        this.mapTo = mapTo;
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

    public boolean shouldMoveTo(CoordSet coordSet) {
        return mapTo.isEmpty() ? false : getNextSet().equals(coordSet);
    }

    public void moveTo(CoordSet coordSet) {
        if (shouldMoveTo(coordSet))
            mapTo.removeFirst();
        else
            mapTo.addFirst(coordSet);
    }

    public boolean hasArrived(CoordSet coordSet) {
        return coordSet.equals(destination);
    }

    @Override
    public void onDisposal(INetworkHandler handler, ThaumicPipePart part, CoordSet coordSet) {
    }

    @Override
    public void process(INetworkHandler handler, ThaumicPipePart part, CoordSet coordSet) {
        if (hasArrived(coordSet)) {
            handler.dispose(this);
            onArrival(part);
            return;
        }

        List<ArmState> armList = part.getArmStateHandler().getPipeConnections();

        boolean moved = false;

        for (ArmState armState : armList) {
            if (shouldMoveTo(coordSet)) {
                moveTo(coordSet);
//                ((IThaumicPipe) armState.getTileEntity()).getPipe().getNetworkHandler().receiveMessage(this);
                moved = true;
                break;
            }
        }

        if (moved) {
            handler.dispose(this);
        } else {
            // TODO Handle no movement, because that means he's lost.
        }
    }

    public abstract void onArrival(ThaumicPipePart part);
}
