package me.jezza.thaumicpipes.common.core.external;

import net.minecraft.tileentity.TileEntity;
import vazkii.tinkerer.common.block.tile.TileRepairer;
import cpw.mods.fml.common.Optional;

public class ThaumicTinkerHelper {
    @Optional.Method(modid = "ThaumicTinkerer")
    public static boolean isRepairer(TileEntity tileEntity) {
        return tileEntity instanceof TileRepairer;
    }
}
