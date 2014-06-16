package me.jezza.thaumicpipes.common.interfaces;

import net.minecraftforge.common.util.ForgeDirection;
import thaumcraft.api.aspects.Aspect;

public interface IThaumicPipe {

    public boolean receiveAspect(Aspect aspect, int amount, ForgeDirection forgeDirection);

    public boolean canReceiveFrom(ForgeDirection forgeDirection);

    public boolean canConnectTo(ForgeDirection forgeDirection);

}
