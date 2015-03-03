package me.jezza.thaumicpipes.common.core;

import me.jezza.thaumicpipes.api.TileProperties;
import me.jezza.thaumicpipes.api.TileType;
import net.minecraftforge.common.util.ForgeDirection;
import thaumcraft.api.aspects.IAspectContainer;
import thaumcraft.api.aspects.IAspectSource;
import thaumcraft.api.aspects.IEssentiaTransport;
import thaumcraft.common.tiles.*;

import static me.jezza.thaumicpipes.api.ThaumicRegistry.blacklistClass;
import static me.jezza.thaumicpipes.api.ThaumicRegistry.registerTile;

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

        // Register default outputs
        registerTile(TileAlembic.class, TileProperties.OUTPUT);
        registerTile(TileFluxScrubber.class, TileProperties.OUTPUT);
        registerTile(TileFluxScrubber.class, TileProperties.OUTPUT);
        registerTile(TileTubeBuffer.class, TileProperties.OUTPUT);

        // Register default storage
        registerTile(TileJarFillable.class, TileProperties.STORAGE);
        registerTile(TileJarFillableVoid.class, TileProperties.STORAGE);
        registerTile(TileEssentiaReservoir.class, TileProperties.STORAGE);

        // Register default inputs
        registerTile(TileThaumatorium.class, TileProperties.INPUT);
        registerTile(TileThaumatoriumTop.class, TileProperties.INPUT);
        registerTile(TileArcaneFurnaceNozzle.class, TileProperties.INPUT);
        registerTile(TileAlchemyFurnaceAdvancedNozzle.class, TileProperties.INPUT);
        registerTile(TileArcaneBoreBase.class, TileProperties.INPUT);
        registerTile(TileArcaneLampFertility.class, TileProperties.INPUT);
        registerTile(TileArcaneLampGrowth.class, TileProperties.INPUT);
        registerTile(TileEssentiaCrystalizer.class, TileProperties.INPUT);

        // Register special outputs
        registerTile(TileCentrifuge.class, TileProperties.OUTPUT.addDirectionalFlag(ForgeDirection.DOWN, TileType.INPUT));
    }
}
