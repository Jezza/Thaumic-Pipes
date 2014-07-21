package me.jezza.thaumicpipes.api;

import java.util.ArrayList;

import me.jezza.thaumicpipes.api.interfaces.IConnectionRegister;
import me.jezza.thaumicpipes.api.registry.Priority;
import me.jezza.thaumicpipes.api.registry.RegistryEntry;
import me.jezza.thaumicpipes.api.registry.RegistryEntry.Type;
import net.minecraft.tileentity.TileEntity;

import com.google.common.collect.Lists;

//@formatter:off
/**
 * 
 * Never register anything from Thaumcraft.
 * I take this as my duty to register it as this mod is made to support it.
 * 
 * An example of an implementation of this class could be something like.
 * 
 * public class ThaumicPipesRegistry extends AbstractRegistry {
 *  @Override 
 *  public void init() { 
 *      register();
 * 
 *      registerSource(Priority.NORMAL, TileSink.class);
 *      registerSource(Priority.LOWEST, TileReallyBigJar.class);
 * 
 *      registerRequester(Priority.NORMAL, TileThaumatorium.class);
 *      registerRequester(Priority.NORMAL, TileThaumatoriumTop.class);
 *      registerRequester(Priority.LOWEST, TileJarFillable.class);
 *  }
 *}
 *
 * Yes, you can only register tileEntities for now.
 * Interfaces might come in later.
 * But you can register abstract tiles.
 * So, If you make a TileReallyBigJar and you extend TileJarFillable, because I've already registered that, you don't have to worry about registering it at all!
 * Same with your own classes, you make a TileSlightlyBigSink, and it extends TileSink, if you register that, it's going to work fine.
 * 
 */
// @formatter:on
public abstract class AbstractRegistry implements IConnectionRegister {

    public ArrayList<RegistryEntry> sourceList = Lists.newArrayList();
    public ArrayList<RegistryEntry> requesterList = Lists.newArrayList();

    private boolean registered = false;

    @Override
    public Iterable<RegistryEntry> getRequesterTileEntities() {
        return requesterList;
    }

    @Override
    public Iterable<RegistryEntry> getSourceTileEntities() {
        return sourceList;
    }

    public void register() {
        if (registered)
            return;
        registered = true;
        ModHelper.addRegister(this);
    }

    public RegistryEntry registerSource(Class<? extends TileEntity> clazz) {
        return registerSource(clazz, Priority.NORMAL);
    }

    public RegistryEntry registerSource(Class<? extends TileEntity> clazz, Priority priority) {
        return registerSource(clazz, priority, 0.0F);
    }

    public RegistryEntry registerSource(Class<? extends TileEntity> clazz, Priority priority, float extensionSize) {
        RegistryEntry entry = new RegistryEntry(Type.SOURCE, clazz, priority, extensionSize);
        sourceList.add(entry);
        return entry;
    }

    public RegistryEntry registerRequester(Class<? extends TileEntity> clazz) {
        return registerRequester(clazz, Priority.NORMAL);
    }

    public RegistryEntry registerRequester(Class<? extends TileEntity> clazz, Priority priority) {
        return registerRequester(clazz, priority, 0.0F);
    }

    public RegistryEntry registerRequester(Class<? extends TileEntity> clazz, Priority priority, float extensionSize) {
        RegistryEntry entry = new RegistryEntry(Type.REQUESTER, clazz, priority, extensionSize);
        requesterList.add(entry);
        return entry;
    }
}
