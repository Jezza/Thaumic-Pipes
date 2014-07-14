package me.jezza.thaumicpipes.api;

import java.util.ArrayList;

import me.jezza.thaumicpipes.api.interfaces.IConnectionRegister;
import me.jezza.thaumicpipes.api.registry.ConnectionRegistry;
import me.jezza.thaumicpipes.api.registry.RegistryEntry;

/**
 * Pretty simple
 */
public class ModHelper {

    private static ArrayList<IConnectionRegister> registerList = new ArrayList<IConnectionRegister>();
    private static boolean postInit = false;

    private ModHelper() {
    }

    /**
     * This is the only method you should ever need.
     * 
     * Don't call anything else in this class.
     */
    public static void addRegister(IConnectionRegister register) {
        if (!postInit)
            registerList.add(register);
    }

    /**
     * Don't call this.
     */
    public static void postInit() {
        if (postInit)
            return;
        postInit = true;

        for (IConnectionRegister register : registerList) {
            for (RegistryEntry entry : register.getSourceTileEntities())
                ConnectionRegistry.registerSource(entry);
            for (RegistryEntry entry : register.getRequesterTileEntities())
                ConnectionRegistry.registerRequester(entry);
        }
    }
}
