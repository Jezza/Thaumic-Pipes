package me.jezza.thaumicpipes.api.registry;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.MathHelper;

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
        this.extensionSize = MathHelper.clamp_float(extensionSize, 0.0F, 1.0F);
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
