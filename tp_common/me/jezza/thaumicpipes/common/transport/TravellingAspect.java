package me.jezza.thaumicpipes.common.transport;

import me.jezza.thaumicpipes.common.core.TPLogger;
import me.jezza.thaumicpipes.common.core.utils.CoordSet;
import me.jezza.thaumicpipes.common.grid.MessageLocated;
import me.jezza.thaumicpipes.common.multipart.pipe.thaumic.ThaumicPipePart;

import java.util.LinkedList;

public class TravellingAspect extends MessageLocated {

    public TravellingAspect(CoordSet destination, LinkedList<CoordSet> mapTo) {
        super(destination, mapTo);
    }

    @Override
    public void onArrival(ThaumicPipePart part) {
        TPLogger.info("Processing Travelling Aspect.");
    }
}
