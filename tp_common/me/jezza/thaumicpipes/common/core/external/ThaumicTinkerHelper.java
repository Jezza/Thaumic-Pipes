package me.jezza.thaumicpipes.common.core.external;

import me.jezza.thaumicpipes.common.core.external.ConnectionRegistry.Priority;
import me.jezza.thaumicpipes.common.interfaces.IConnectionRegister;
import net.minecraft.tileentity.TileEntity;
import vazkii.tinkerer.common.block.tile.TileRepairer;
import cpw.mods.fml.common.Optional;

public class ThaumicTinkerHelper implements IConnectionRegister {

    @Override
    public void init() {
        ConnectionRegistry.registerRequester(Priority.NORMAL, TileRepairer.class);
    }

    @Optional.Method(modid = "ThaumicTinkerer")
    public static boolean isRepairer(TileEntity tileEntity) {
        return tileEntity instanceof TileRepairer;
    }

}
