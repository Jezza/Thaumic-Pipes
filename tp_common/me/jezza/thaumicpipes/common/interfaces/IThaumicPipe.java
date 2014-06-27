package me.jezza.thaumicpipes.common.interfaces;

import java.util.HashSet;

import me.jezza.thaumicpipes.common.core.AspectContainerList;
import me.jezza.thaumicpipes.common.core.utils.CoordSet;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.util.ForgeDirection;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;

public interface IThaumicPipe {

    public AspectList getAspectList();

    public AspectContainerList getContainerState();

    public boolean receiveAspect(Aspect aspect, int amount, ForgeDirection forgeDirection);

    public AspectList removeAspect(Aspect aspect, int amount);

    public boolean reduceAspect(Aspect aspect, int amount);

    public boolean canReceiveFrom(ForgeDirection forgeDirection);

    public boolean canConnectTo(ForgeDirection direction);

    public AspectContainerList ping(Aspect pingedAspect, HashSet<CoordSet> pipeList);

    public void drain();
}
