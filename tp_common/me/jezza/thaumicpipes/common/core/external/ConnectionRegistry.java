package me.jezza.thaumicpipes.common.core.external;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map.Entry;

import me.jezza.thaumicpipes.common.interfaces.IThaumicPipe;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.util.ForgeDirection;
import thaumcraft.api.aspects.IEssentiaTransport;
import thaumcraft.common.tiles.TileJarFillable;

import com.google.common.collect.HashMultimap;

public class ConnectionRegistry {

    private static int EXPECTED_VALUES = 10;
    private static int EXPECTED_KEYS = Priority.values().length;

    /**
     * A priority value of lowest is a passive connection.
     * 
     * A default route, more or less.
     */
    private static HashMultimap<Priority, Class<? extends TileEntity>> sourceRegistry = HashMultimap.create(EXPECTED_KEYS, EXPECTED_VALUES);
    private static HashMultimap<Priority, Class<? extends TileEntity>> requesterRegistry = HashMultimap.create(EXPECTED_KEYS, EXPECTED_VALUES);

    private static Priority getPriorityOf(HashMultimap<Priority, Class<? extends TileEntity>> map, TileEntity tileEntity) {
        for (Entry<Priority, Class<? extends TileEntity>> entrySet : map.entries())
            if (entrySet.getValue().isInstance(tileEntity))
                return entrySet.getKey();
        return null;
    }

    private static Class<? extends TileEntity> getClassOf(HashMultimap<Priority, Class<? extends TileEntity>> map, TileEntity tileEntity) {
        for (Class<? extends TileEntity> clazz : map.values())
            if (clazz.isInstance(tileEntity))
                return clazz;
        return null;
    }

    private static boolean confimConnection(Class<? extends TileEntity> clazz, ForgeDirection direction, TileEntity tileEntity) {
        if (clazz == null)
            return false;
        if (tileEntity instanceof TileJarFillable)
            return true;
        if (tileEntity instanceof IEssentiaTransport)
            return ((IEssentiaTransport) tileEntity).isConnectable(direction.getOpposite());
        return true;
    }

    public static boolean isValidConnection(TileEntity tileEntity, ForgeDirection direction) {
        if (tileEntity instanceof IThaumicPipe)
            return true;

        if (confimConnection(getClassOf(sourceRegistry, tileEntity), direction, tileEntity))
            return true;
        if (confimConnection(getClassOf(requesterRegistry, tileEntity), direction, tileEntity))
            return true;
        return false;
    }

    public static List<TileEntity> sortSourcePriority(TileEntity... tileEntities) {
        return sortPriority(sourceRegistry, tileEntities);
    }

    public static List<TileEntity> sortRequesterPriority(TileEntity... tileEntities) {
        return sortPriority(requesterRegistry, tileEntities);
    }

    private static List<TileEntity> sortPriority(HashMultimap<Priority, Class<? extends TileEntity>> map, TileEntity... tileEntities) {
        List<TileEntity> tileList = Arrays.asList(tileEntities);
        List<TileEntity> sortedList = new ArrayList<TileEntity>();

        for (TileEntity tileEntity : tileList) {
            boolean added = false;
            Priority priority = getPriorityOf(map, tileEntity);

            int index = 0;
            for (TileEntity tempTile : sortedList) {
                Priority otherPriority = getPriorityOf(map, tempTile);
                index++;
                if (otherPriority.ordinal() <= priority.ordinal()) {
                    sortedList.add(index, tileEntity);
                    added = true;
                    break;
                }
            }

            if (!added)
                sortedList.add(tileEntity);
        }

        return sortedList;
    }

    private static boolean exists(HashMultimap<Priority, Class<? extends TileEntity>> map, TileEntity tileEntity) {
        if (getClassOf(sourceRegistry, tileEntity) != null)
            return true;
        if (getClassOf(requesterRegistry, tileEntity) != null)
            return true;
        return false;
    }

    public static void registerSource(Priority priority, Class<? extends TileEntity> clazz) {
        sourceRegistry.put(priority, clazz);
    }

    public static boolean isSource(TileEntity tileEntity) {
        return exists(sourceRegistry, tileEntity);
    }

    public static void registerRequester(Priority priority, Class<? extends TileEntity> clazz) {
        requesterRegistry.put(priority, clazz);
    }

    public static boolean isRequester(TileEntity tileEntity) {
        return exists(requesterRegistry, tileEntity);
    }

    public static enum Priority {
        LOWEST, LOWER, NORMAL, HIGH, HIGHEST, SEVERE;
    }
}
