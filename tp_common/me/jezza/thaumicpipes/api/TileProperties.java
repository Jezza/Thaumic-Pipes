package me.jezza.thaumicpipes.api;

import net.minecraftforge.common.util.ForgeDirection;

import java.util.EnumMap;
import java.util.Map;

public class TileProperties {
    /**
     * For tiles that solely provide essentia and will NEVER need/require essentia to be delivered to them.
     * EG,
     * {@link thaumcraft.common.tiles.TileAlembic}
     * {@link thaumcraft.common.tiles.TileFluxScrubber}
     */
    public static final TileProperties INPUT = new TileProperties(TileType.INPUT, true);

    /**
     * For tiles that hold it for use of other tiles. Don't need/require it, but can hold it. (This can also mean processors, such as the Centrifuge)
     * EG,
     * {@link thaumcraft.common.tiles.TileJarFillable}
     * {@link thaumcraft.common.tiles.TileTubeBuffer}
     * {@link thaumcraft.common.tiles.TileEssentiaReservoir}
     */
    public static final TileProperties STORAGE = new TileProperties(TileType.STORAGE, true);

    /**
     * For tiles that solely take essentia from the network and will NEVER provide essentia back into the network.
     * EG,
     * {@link thaumcraft.common.tiles.TileThaumatorium}
     * {@link thaumcraft.common.tiles.TileThaumatoriumTop}
     * {@link thaumcraft.common.tiles.TileArcaneFurnaceNozzle}
     * {@link thaumcraft.common.tiles.TileAlchemyFurnaceAdvancedNozzle}
     * {@link thaumcraft.common.tiles.TileArcaneBoreBase}
     * {@link thaumcraft.common.tiles.TileArcaneLampFertility}
     * {@link thaumcraft.common.tiles.TileArcaneLampGrowth}
     * {@link thaumcraft.common.tiles.TileEssentiaCrystalizer}
     */
    public static final TileProperties OUTPUT = new TileProperties(TileType.OUTPUT, true);

    // Root type. If no special direction properties were found, default to this.
    public final TileType tileType;
    public final boolean lockFlag;
    private Map<ForgeDirection, TileType> directionMap = new EnumMap<>(ForgeDirection.class);

    public TileProperties(TileType tileType, boolean lockFlag) {
        this.tileType = tileType;
        this.lockFlag = lockFlag;
    }

    /**
     * This allows the ability to add directional overrides.
     * EG,
     * {@link thaumcraft.common.tiles.TileCentrifuge}
     * The centrifuge needs DOWN to act as an OUTPUT for the network, (Thus allowing essentia to be placed inside it),
     * and UP to be labeled as an INPUT for the network. (Thus allowing the pipes to pull out)
     */
    public TileProperties(TileType tileType) {
        this(tileType, false);
    }

    public TileProperties addDirectionalFlag(ForgeDirection direction, TileType tileType) {
        if (lockFlag) {
            TileProperties tileProperties = new TileProperties(tileType);
            return tileProperties.addDirectionalFlag(direction, tileType);
        }
        directionMap.put(direction, tileType);
        return this;
    }

    public TileType getTileType(ForgeDirection direction) {
        return directionMap.containsKey(direction) ? directionMap.get(direction) : tileType;
    }
}
