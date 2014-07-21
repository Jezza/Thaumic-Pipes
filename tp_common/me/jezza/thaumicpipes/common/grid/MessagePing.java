package me.jezza.thaumicpipes.common.grid;

import me.jezza.thaumicpipes.common.core.TPLogger;
import me.jezza.thaumicpipes.common.core.utils.CoordSet;
import me.jezza.thaumicpipes.common.grid.interfaces.IMessageOrigin;
import me.jezza.thaumicpipes.common.multipart.pipe.thaumic.ThaumicPipePart;

public class MessagePing extends MessageLocating {

    public MessagePing(IMessageOrigin origin, CoordSet coordSet) {
        super(origin);
        getVisitedSet().add(coordSet);
    }

    @Override
    public boolean shouldDisposeOf(ThaumicPipePart part, CoordSet coordSet) {
        return false;
    }

    @Override
    public void process(ThaumicPipePart part, CoordSet coordSet) {
        TPLogger.info(getVisitedSet());
    }

    @Override
    public void onDisposal(ThaumicPipePart part, CoordSet coordSet) {
    }

}
