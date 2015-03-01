package me.jezza.thaumicpipes.api;

import me.jezza.thaumicpipes.api.interfaces.IThaumicInput;
import me.jezza.thaumicpipes.api.interfaces.IThaumicOutput;
import me.jezza.thaumicpipes.api.interfaces.IThaumicStorage;

import java.util.*;

/**
 * This gets locked on {@link cpw.mods.fml.common.event.FMLPostInitializationEvent},
 * So, have your shit registered before then, and you'll be fine.
 * <p/>
 * I blacklist a bunch of classes that stops blanket casing.
 * (A word I just coined. It means cases that are generalised by specific cause or means.)
 * <p/>
 * If you have a ton of Tiles that aren't related to each other, but would be registered under the same TileType,
 * why not make an empty interface, and register that?
 * <p/>
 * EG,
 * TileEssentiaDeleter doesn't extend any pre-registered tiles, so naturally, you have to register it.
 * But you've also made TileEssentiaRemover, along with other class names that I can't think of, because who would do that in the first place...
 * ANYWAY, create an empty interface, eg, IAwesomeEssentiaRemoverDeleterOutput, and register that.
 * registerTile(IAwesomeEssentiaRemoverDeleter.class, TileType.INPUT)
 * <p/>
 * Or better yet, just use one of my pre-handled interfaces:
 * {@link me.jezza.thaumicpipes.api.interfaces.IThaumicInput}
 * {@link me.jezza.thaumicpipes.api.interfaces.IThaumicOutput}
 * {@link me.jezza.thaumicpipes.api.interfaces.IThaumicStorage}
 * <p/>
 * You should be able to distinguish what interface is handled as what type...
 * <p/>
 * Examples:
 * <p/>
 * registerTile(TileAlembic.class, TileProperties.OUTPUT);
 * <p/>
 * registerTile(TileJarFillable.class, TileProperties.STORAGE);
 * <p/>
 * registerTile(TileArcaneLampFertility.class, TileProperties.INPUT);
 * registerTile(TileArcaneLampGrowth.class, TileProperties.INPUT);
 * <p/>
 * <p/>
 * Overloading the tile types:
 * registerTile(TileCentrifuge.class, TileProperties.OUTPUT.addDirectionalFlag(ForgeDirection.DOWN, TileType.INPUT));
 */
public class ThaumicRegistry {

    private static boolean locked = false;

    private static Map<Class<?>, TileProperties> registeredClasses = new LinkedHashMap<>(64);
    private static final List<Class<?>> blacklistedClasses = new ArrayList<>();

    static {
        blacklistedClasses.add(IThaumicInput.class);
        blacklistedClasses.add(IThaumicStorage.class);
        blacklistedClasses.add(IThaumicOutput.class);
    }

    public static void blacklistClass(Class<?> clazz) {
        if (!blacklistedClasses.contains(clazz))
            blacklistedClasses.add(clazz);
    }

    /**
     * Registering a class will also account for all subtypes.
     * EG,
     * registerTile(TileJarFillable.class)
     * Will account for all subtypes if they need no special consideration.
     *
     * @param clazz          The clazz to be registered.
     * @param tileProperties The type the class should registered with.
     * @return True - if the class is registered.
     */
    public static boolean registerTile(Class<?> clazz, TileProperties tileProperties) {
        if (locked)
            throw new RuntimeException("Registry has already been locked down.");
        if (clazz == null || tileProperties == null)
            return false;

        for (Class<?> blacklistedClass : blacklistedClasses)
            if (clazz == blacklistedClass)
                return false;

        if (!registeredClasses.containsKey(clazz))
            registeredClasses.put(clazz, tileProperties);
        return registeredClasses.containsKey(clazz);
    }

    /**
     * Ignore this, unless you know what you're doing.
     */
    public static Map<Class<?>, TileProperties> getRegisteredClasses() {
        return locked ? registeredClasses : null;
    }

    /**
     * No touchy.
     */
    public static void lock() {
        locked = true;
        registeredClasses = Collections.unmodifiableMap(registeredClasses);
    }
}

