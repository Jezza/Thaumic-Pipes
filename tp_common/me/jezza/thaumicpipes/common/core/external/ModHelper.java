package me.jezza.thaumicpipes.common.core.external;

import cpw.mods.fml.common.Loader;

public class ModHelper {

    public static boolean isThaumicTinkererLoaded() {
        return Loader.isModLoaded("ThaumicTinkerer");
    }

}
