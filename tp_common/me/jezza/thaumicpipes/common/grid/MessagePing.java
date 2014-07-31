package me.jezza.thaumicpipes.common.grid;

import me.jezza.thaumicpipes.common.core.TPLogger;
import me.jezza.thaumicpipes.common.core.utils.CoordSet;
import me.jezza.thaumicpipes.common.grid.interfaces.IMessageOrigin;
import me.jezza.thaumicpipes.common.grid.interfaces.INetworkHandler;
import me.jezza.thaumicpipes.common.multipart.pipe.thaumic.ThaumicPipePart;
import thaumcraft.api.aspects.Aspect;

public class MessagePing extends MessageLocating {

    private Aspect targetAspect;

    public MessagePing(IMessageOrigin origin, CoordSet coordSet) {
        super(origin);
        getVisitedSet().add(coordSet);
    }

    public MessagePing setTargetAspect(Aspect targetAspect) {
        this.targetAspect = targetAspect;
        return this;
    }


    @Override
    public void process(INetworkHandler handler, ThaumicPipePart part, CoordSet coordSet) {


        super.process(handler, part, coordSet);
    }

    @Override
    public void onFinalRemoval(ThaumicPipePart part) {
        TPLogger.info(part.getCoordSet());
        TPLogger.info("CALLED");
    }
}
