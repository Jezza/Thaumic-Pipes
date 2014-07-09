package me.jezza.thaumicpipes.common.transport.connection;

import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.util.ForgeDirection;

public class TileEntityWrapper {

    public TileEntity tileEntity;
    public ForgeDirection direction;

    public TileEntityWrapper(TileEntity tileEntity, ForgeDirection direction) {
        this.tileEntity = tileEntity;
        this.direction = direction;
    }

}
