package me.jezza.thaumicpipes.api.registry;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.MathHelper;

public class RegistryEntry {

    private final Type type;
    private final Class<? extends TileEntity> clazz;
    private final Priority priority;
    private final float extensionSize;

    public RegistryEntry(Type type, Class<? extends TileEntity> clazz) {
        this(type, clazz, Priority.NORMAL, 0.0F);
    }

    public RegistryEntry(Type type, Class<? extends TileEntity> clazz, Priority priority) {
        this(type, clazz, priority, 0.0F);
    }

    public RegistryEntry(Type type, Class<? extends TileEntity> clazz, Priority priority, float extensionSize) {
        this.type = type;
        this.clazz = clazz;
        this.priority = priority;
        this.extensionSize = MathHelper.clamp_float(extensionSize, 0.0F, 1.0F);
    }

    public Class<? extends TileEntity> getClazz() {
        return clazz;
    }

    public Type getType() {
        return type;
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

    public static enum Type {
        SOURCE, REQUESTER, PIPE;

        public boolean isSource() {
            return this == SOURCE;
        }

        public boolean isRequester() {
            return this == REQUESTER;
        }

        public boolean isPipe() {
            return this == PIPE;
        }
    }
}
