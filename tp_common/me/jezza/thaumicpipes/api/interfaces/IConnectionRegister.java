package me.jezza.thaumicpipes.api.interfaces;

import me.jezza.thaumicpipes.api.registry.RegistryEntry;

public interface IConnectionRegister {

    public void init();

    /**
     * @return A iterable object for registering the appropriate tileEntities
     */
    public Iterable<RegistryEntry> getSourceTileEntities();

    /**
     * @return A iterable object for registering the appropriate tileEntities
     */
    public Iterable<RegistryEntry> getRequesterTileEntities();

}
