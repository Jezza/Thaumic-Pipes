package me.jezza.thaumicpipes.common.core.utils;

import net.minecraft.tileentity.TileEntity;

public class CoordSet {

    int x, y, z;

    public CoordSet(int x, int y, int z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public CoordSet(TileEntity tileEntity) {
        this(tileEntity.xCoord, tileEntity.yCoord, tileEntity.zCoord);
    }

    @Override
    public String toString() {

        return "@ X: " + x + ", Y: " + y + ", Z: " + z;
    }

}
