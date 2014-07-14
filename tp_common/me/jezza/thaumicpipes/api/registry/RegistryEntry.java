package me.jezza.thaumicpipes.api.registry;

import net.minecraft.tileentity.TileEntity;

public class RegistryEntry {

    private final Class<? extends TileEntity> clazz;
    private final Priority priority;
    private final float extensionSize;

    public RegistryEntry(Class<? extends TileEntity> clazz) {
        this(clazz, Priority.NORMAL, 0.0F);
    }

    public RegistryEntry(Class<? extends TileEntity> clazz, Priority priority) {
        this(clazz, priority, 0.0F);
    }

    public RegistryEntry(Class<? extends TileEntity> clazz, Priority priority, float extensionSize) {
        this.clazz = clazz;
        this.priority = priority;
        this.extensionSize = extensionSize;
    }

    public Class<? extends TileEntity> getClazz() {
        return clazz;
    }

    public boolean isInstance(TileEntity tileEntity) {
        return clazz.isInstance(tileEntity);
    }

    public Priority getPriority() {
        return priority;
    }

    public float getExtensionSize() {
        return extensionSize;
    }

    public boolean isValid() {
        return clazz != null;
    }

    @Override
    public int hashCode() {
        return clazz.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null || !(obj instanceof RegistryEntry))
            return false;
        return ((RegistryEntry) obj).clazz.equals(clazz);
    }
}
