package me.jezza.thaumicpipes.common.tileentity;

import me.jezza.thaumicpipes.common.core.utils.CoordSet;
import net.minecraft.tileentity.TileEntity;

public class TileTP extends TileEntity {

    public CoordSet getCoordSet() {
        return new CoordSet(xCoord, yCoord, zCoord);
    }

}
