package me.jezza.thaumicpipes.common.core.utils;

import cpw.mods.fml.common.Loader;

public class Utils {

    public static boolean isThaumicTinkererLoaded() {
        return Loader.isModLoaded("ThaumicTinkerer");
    }

}
