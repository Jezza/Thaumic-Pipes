package me.jezza.thaumicpipes.common.core.interfaces;

import me.jezza.oc.common.utils.CoordSet;
import me.jezza.thaumicpipes.common.multipart.pipe.PipePartAbstract;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.util.ForgeDirection;

public interface IThaumicPipe {

    public boolean canConnectTo(TileEntity tileEntity, ForgeDirection direction);

    public CoordSet getCoordSet();

    public PipePartAbstract getPipe();
}
