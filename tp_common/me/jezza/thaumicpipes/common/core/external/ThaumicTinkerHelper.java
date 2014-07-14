package me.jezza.thaumicpipes.common.core.external;

import me.jezza.thaumicpipes.api.AbstractRegistry;
import me.jezza.thaumicpipes.api.registry.Priority;
import net.minecraft.tileentity.TileEntity;
import vazkii.tinkerer.common.block.tile.TileRepairer;
import cpw.mods.fml.common.Optional;

public class ThaumicTinkerHelper extends AbstractRegistry {

    @Override
    public void init() {
        register();

        registerRequester(TileRepairer.class, Priority.NORMAL, 0.0F);
    }

    @Optional.Method(modid = "ThaumicTinkerer")
    public static boolean isRepairer(TileEntity tileEntity) {
        return tileEntity instanceof TileRepairer;
    }

}
