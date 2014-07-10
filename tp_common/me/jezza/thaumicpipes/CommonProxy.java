package me.jezza.thaumicpipes;

import me.jezza.thaumicpipes.common.lib.Strings;
import me.jezza.thaumicpipes.common.tileentity.TileThaumicPipe;
import cpw.mods.fml.common.registry.GameRegistry;

public class CommonProxy {

    public void registerTileEntities() {
        GameRegistry.registerTileEntity(TileThaumicPipe.class, Strings.THAUMIC_PIPE);
    }

    public void initServerSide() {

    }

    public void initClientSide() {
    }
}
