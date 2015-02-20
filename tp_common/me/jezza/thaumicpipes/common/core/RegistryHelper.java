package me.jezza.thaumicpipes.common.core;

import me.jezza.thaumicpipes.api.ThaumicRegistry;
import me.jezza.thaumicpipes.api.TileProperties;
import me.jezza.thaumicpipes.api.TileType;
import net.minecraftforge.common.util.ForgeDirection;
import thaumcraft.api.aspects.IAspectContainer;
import thaumcraft.api.aspects.IAspectSource;
import thaumcraft.api.aspects.IEssentiaTransport;
import thaumcraft.common.tiles.*;

/**
 * Used to register the default tiles that the network supports.
 */
public class RegistryHelper {

    private static boolean init = false;

    public static void init() {
        if (init)
            return;
        init = true;

        blacklistClass(IEssentiaTransport.class);
        blacklistClass(IAspectContainer.class);
        blacklistClass(IAspectSource.class);

        // Register default inputs
        registerTile(TileAlembic.class, TileProperties.INPUT);
        registerTile(TileFluxScrubber.class, TileProperties.INPUT);

        // Register default storage
        registerTile(TileJarFillable.class, TileProperties.STORAGE);
        registerTile(TileJarFillableVoid.class, TileProperties.STORAGE);
        registerTile(TileTubeBuffer.class, TileProperties.STORAGE);
        registerTile(TileEssentiaReservoir.class, TileProperties.STORAGE);

        // Register default outputs
        registerTile(TileThaumatorium.class, TileProperties.OUTPUT);
        registerTile(TileThaumatoriumTop.class, TileProperties.OUTPUT);
        registerTile(TileArcaneFurnaceNozzle.class, TileProperties.OUTPUT);
        registerTile(TileAlchemyFurnaceAdvancedNozzle.class, TileProperties.OUTPUT);
        registerTile(TileArcaneBoreBase.class, TileProperties.OUTPUT);
        registerTile(TileArcaneLampFertility.class, TileProperties.OUTPUT);
        registerTile(TileArcaneLampGrowth.class, TileProperties.OUTPUT);
        registerTile(TileEssentiaCrystalizer.class, TileProperties.OUTPUT);

        // Register special outputs
        registerTile(TileCentrifuge.class, TileProperties.OUTPUT.addDirectionalFlag(ForgeDirection.DOWN, TileType.INPUT));

    }

    private static void blacklistClass(Class<?> clazz) {
        ThaumicRegistry.blacklistClass(clazz);
    }

    private static void registerTile(Class<?> clazz, TileProperties tileProperties) {
        ThaumicRegistry.registerTile(clazz, tileProperties);
    }
}
