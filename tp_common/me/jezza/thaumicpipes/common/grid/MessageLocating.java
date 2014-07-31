package me.jezza.thaumicpipes.common.grid;

import me.jezza.thaumicpipes.api.interfaces.IThaumicPipe;
import me.jezza.thaumicpipes.common.core.utils.CoordSet;
import me.jezza.thaumicpipes.common.grid.interfaces.IMessageOrigin;
import me.jezza.thaumicpipes.common.grid.interfaces.INetworkHandler;
import me.jezza.thaumicpipes.common.grid.interfaces.INetworkMessage;
import me.jezza.thaumicpipes.common.multipart.pipe.thaumic.ThaumicPipePart;
import me.jezza.thaumicpipes.common.transport.connection.ArmState;

import java.util.LinkedList;
import java.util.List;

public abstract class MessageLocating implements INetworkMessage {

    private int instances;
    private IMessageOrigin origin;
    private LinkedList<CoordSet> visitedSet;

    public MessageLocating(IMessageOrigin origin) {
        instances = 1;
        this.origin = origin;
        this.visitedSet = new LinkedList<CoordSet>();
    }

    public MessageLocating setOrigin(IMessageOrigin origin) {
        this.origin = origin;
        return this;
    }

    public boolean shouldMoveTo(CoordSet coordSet) {
        return !visitedSet.contains(coordSet);
    }

    public void moveTo(CoordSet coordSet) {
        visitedSet.add(coordSet);
    }

    public LinkedList<CoordSet> getVisitedSet() {
        return visitedSet;
    }

    public IMessageOrigin getOrigin() {
        return origin;
    }

    public void flushVisitedSet() {
        visitedSet.clear();
    }

    public void addInstance() {
        instances++;
    }

    public boolean removeInstance() {
        return --instances <= 0;
    }

    @Override
    public void onDisposal(INetworkHandler handler, ThaumicPipePart part, CoordSet coordSet) {
    }

    @Override
    public void process(INetworkHandler handler, ThaumicPipePart part, CoordSet coordSet) {
        List<ArmState> armList = part.getArmStateHandler().getPipeConnections();

        for (ArmState armState : armList) {
            CoordSet armSet = armState.getCoordSet();
            if (shouldMoveTo(armSet)) {
                addInstance();
                moveTo(armSet);
//                ((IThaumicPipe) armState.getTileEntity()).getPipe().getNetworkHandler().receiveMessage(this);
            }
        }

        handler.dispose(this);
        if (removeInstance())
            onFinalRemoval(part);
    }

    public abstract void onFinalRemoval(ThaumicPipePart part);
}
