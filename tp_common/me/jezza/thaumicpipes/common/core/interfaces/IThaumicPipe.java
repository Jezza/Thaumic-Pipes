package me.jezza.thaumicpipes.common.core.interfaces;

import me.jezza.oc.common.utils.CoordSet;
import me.jezza.thaumicpipes.common.multipart.pipe.PipePartAbstract;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.util.ForgeDirection;
import thaumcraft.api.aspects.AspectList;

import java.util.Collection;

public interface IThaumicPipe {

    public boolean canConnectTo(TileEntity tileEntity, ForgeDirection direction);

    public CoordSet getCoordSet();

    public Collection<TileEntity> getJarConnections();

    public Collection<TileEntity> getContainerConnections();

    public Collection<TileEntity> getAlbemicConnections();

    public Collection<TileEntity> getConstructConnections();

    public AspectList getPendingAspects();

    public PipePartAbstract getPipe();
}
