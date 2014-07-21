package me.jezza.thaumicpipes.common.transport;

import me.jezza.thaumicpipes.common.core.TPLogger;
import me.jezza.thaumicpipes.common.core.utils.CoordSet;
import me.jezza.thaumicpipes.common.grid.MessageLocated;
import me.jezza.thaumicpipes.common.multipart.pipe.thaumic.ThaumicPipePart;

public class TravellingAspect extends MessageLocated {

    public TravellingAspect(CoordSet destination) {
        super(destination);
    }

    @Override
    public void onDisposal(ThaumicPipePart part, CoordSet coordSet) {
        TPLogger.info("Processing Travelling Aspect.");
    }
}
