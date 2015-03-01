package me.jezza.thaumicpipes.api;

import net.minecraftforge.common.util.ForgeDirection;

import java.util.EnumMap;
import java.util.Map;

public class TileProperties {

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
     * For tiles that solely provide essentia and will NEVER need/require essentia to be delivered to them.
     * EG,
     * {@link thaumcraft.common.tiles.TileAlembic}
     * {@link thaumcraft.common.tiles.TileFluxScrubber}
     */
    public static final TileProperties OUTPUT = new TileProperties(TileType.OUTPUT, true);


    // Root type. If no special direction properties were found, default to this.
    public final TileType rootTileType;
    public final boolean lockFlag;
    private Map<ForgeDirection, TileType> directionMap = new EnumMap<>(ForgeDirection.class);

    public TileProperties(TileType tileType, boolean lockFlag) {
        this.rootTileType = tileType;
        this.lockFlag = lockFlag;
    }

    /**
     * This allows the ability to add directional overrides.
     * EG,
     * {@link thaumcraft.common.tiles.TileCentrifuge}
     * The centrifuge needs DOWN to act as an INPUT, (Thus allowing essentia to be placed inside it),
     * and UP to be labeled as an OUTPUT for the network. (Thus allowing the pipes to pull out)
     */
    public TileProperties(TileType tileType) {
        this(tileType, false);
    }

    /**
     * Adds the directionalFlag to a map to be used internally.
     *
     * @param direction - The direction that the tileType is applied to.
     * @param tileType  - TileType the flag should be set as.
     * @return - the currentTile properties, OR if the object was locked, then a new TileProperties with the flag added.
     */
    public TileProperties addDirectionalFlag(ForgeDirection direction, TileType tileType) {
        if (lockFlag) {
            TileProperties tileProperties = new TileProperties(rootTileType);
            return tileProperties.addDirectionalFlag(direction, tileType);
        }
        directionMap.put(direction, tileType);
        return this;
    }

    /**
     * Grabs the tileType given a direction.
     * <p/>
     * Defaults to the rootTileType if no directionalFlag was set.
     *
     * @param direction - Direction to check.
     * @return the specific directional flag, or in the case of none being found, the rootTileType
     */
    public TileType getTileType(ForgeDirection direction) {
        return directionMap.containsKey(direction) ? directionMap.get(direction) : rootTileType;
    }
}
