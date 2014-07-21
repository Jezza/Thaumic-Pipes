package me.jezza.thaumicpipes.api.registry;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import me.jezza.thaumicpipes.common.core.TPLogger;
import net.minecraft.tileentity.TileEntity;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

/**
 * No touchy
 */
public class ConnectionRegistry {

    private static int EXPECTED_SIZE = 32;

    /**
     * A priority value of lowest is a passive connection.
     * 
     * A default route, more or less.
     */
    private static HashMap<Class<? extends TileEntity>, RegistryEntry> sourceRegistry = Maps.newHashMapWithExpectedSize(EXPECTED_SIZE);
    private static HashMap<Class<? extends TileEntity>, RegistryEntry> requesterRegistry = Maps.newHashMapWithExpectedSize(EXPECTED_SIZE);

    public static RegistryEntry getRegistryEntry(TileEntity tileEntity) {
        if (tileEntity == null)
            return null;
        RegistryEntry entry = getRegistryEntry(sourceRegistry, tileEntity);
        if (entry == null)
            entry = getRegistryEntry(requesterRegistry, tileEntity);
        return entry;
    }

    private static RegistryEntry getRegistryEntry(HashMap<Class<? extends TileEntity>, RegistryEntry> map, TileEntity tileEntity) {
        Class<? extends TileEntity> clazz = getClassOf(map, tileEntity);
        if (clazz == null)
            return null;
        return map.get(clazz);
    }

    private static Class<? extends TileEntity> getClassOf(HashMap<Class<? extends TileEntity>, RegistryEntry> map, TileEntity tileEntity) {
        for (Class<? extends TileEntity> clazz : map.keySet())
            if (clazz.isInstance(tileEntity))
                return clazz;
        return null;
    }

    public static boolean isValidConnection(TileEntity tileEntity) {
        if (tileEntity == null)
            return false;
        if (isSource(tileEntity))
            return true;
        if (isRequester(tileEntity))
            return true;
        return false;
    }

    public static List<TileEntity> sortSourcePriority(TileEntity... tileEntities) {
        return sortPriority(sourceRegistry, tileEntities);
    }

    public static List<TileEntity> sortRequesterPriority(TileEntity... tileEntities) {
        return sortPriority(requesterRegistry, tileEntities);
    }

    private static List<TileEntity> sortPriority(HashMap<Class<? extends TileEntity>, RegistryEntry> map, TileEntity... tileEntities) {
        List<TileEntity> tileList = Lists.newArrayList();

        for (TileEntity tileEntity : tileEntities)
            if (exists(map, tileEntity))
                tileList.add(tileEntity);

        return sortPriority(map, tileList);
    }

    private static List<TileEntity> sortPriority(HashMap<Class<? extends TileEntity>, RegistryEntry> map, Iterable<TileEntity> tileEntities) {
        List<TileEntity> sortedList = new ArrayList<TileEntity>();

        for (TileEntity tileEntity : tileEntities) {
            boolean added = false;
            Priority priority = getRegistryEntry(map, tileEntity).getPriority();

            int index = 0;
            for (TileEntity tempTile : sortedList) {
                Priority otherPriority = getRegistryEntry(map, tempTile).getPriority();
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

    private static boolean register(HashMap<Class<? extends TileEntity>, RegistryEntry> map, RegistryEntry entry) {
        if (entry == null)
            throw new IllegalArgumentException("Invalid argument: entry should not be null.");
        if (entry.getClazz() == null)
            throw new IllegalArgumentException("Invalid argument: entry clazz should not be null");
        if (entry.getPriority() == null)
            throw new IllegalArgumentException("Invalid argument: entry clazz should not be null");

        Class<? extends TileEntity> clazz = entry.getClazz();
        if (!map.containsKey(clazz))
            map.put(clazz, entry);
        boolean flag = map.containsKey(clazz);
        if (!flag)
            TPLogger.severe("Failed to add ");
        return flag;
    }

    private static boolean exists(HashMap<Class<? extends TileEntity>, RegistryEntry> map, TileEntity tileEntity) {
        return getClassOf(map, tileEntity) != null;
    }

    public static boolean registerSource(RegistryEntry entry) {
        return register(sourceRegistry, entry);
    }

    public static boolean isSource(TileEntity tileEntity) {
        return exists(sourceRegistry, tileEntity);
    }

    public static boolean registerRequester(RegistryEntry entry) {
        return register(requesterRegistry, entry);
    }

    public static boolean isRequester(TileEntity tileEntity) {
        return exists(requesterRegistry, tileEntity);
    }
}
