package me.jezza.thaumicpipes.common.core.external;

import java.util.ArrayList;

import me.jezza.thaumicpipes.common.interfaces.IConnectionRegister;
import cpw.mods.fml.common.Loader;

public class ModHelper {

    private static final ModHelper INSTANCE = new ModHelper();

    private static ArrayList<IConnectionRegister> registerList = new ArrayList<IConnectionRegister>();
    private static boolean init = false;

    private ModHelper() {
    }

    public static ModHelper getInstance() {
        return INSTANCE;
    }

    public static boolean isThaumicTinkererLoaded() {
        return Loader.isModLoaded("ThaumicTinkerer");
    }

    public ModHelper init() {
        if (init)
            return this;
        init = true;

        registerList.add(new ThaumcraftHelper());

        if (isThaumicTinkererLoaded())
            registerList.add(new ThaumicTinkerHelper());

        return this;
    }

    public void registerConnectionTypes() {
        for (IConnectionRegister register : registerList)
            register.init();
    }

}
