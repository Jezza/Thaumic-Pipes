package me.jezza.thaumicpipes;

import cpw.mods.fml.common.registry.GameRegistry;
import me.jezza.thaumicpipes.common.lib.Strings;
import me.jezza.thaumicpipes.common.tileentity.TileThaumicPipe;

public class CommonProxy {

    public void registerTileEntities() {
        GameRegistry.registerTileEntity(TileThaumicPipe.class, Strings.THAUMIC_PIPE);
    }

    public void initServerSide() {

    }

    public void initClientSide() {
    }
}
