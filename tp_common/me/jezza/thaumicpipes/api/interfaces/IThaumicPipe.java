package me.jezza.thaumicpipes.api.interfaces;

import me.jezza.thaumicpipes.common.multipart.pipe.PipePartAbstract;
import me.jezza.thaumicpipes.common.transport.TravellingAspect;
import net.minecraftforge.common.util.ForgeDirection;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;

public interface IThaumicPipe {

    public void addTravellingAspect(TravellingAspect tA);

    public void processTravellingAspects();

    public AspectList getAspectList();

    public boolean addAspect(Aspect aspect, int amount, ForgeDirection forgeDirection);

    public AspectList removeAspect(Aspect aspect, int amount);

    public boolean reduceAspect(Aspect aspect, int amount);

    public boolean canReceiveFrom(ForgeDirection forgeDirection);

    public boolean canConnectTo(ForgeDirection direction);

    public void drain();

    public PipePartAbstract getPipe();
}
