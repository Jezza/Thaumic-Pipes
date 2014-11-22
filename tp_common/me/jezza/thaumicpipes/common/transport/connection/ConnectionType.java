package me.jezza.thaumicpipes.common.transport.connection;

import me.jezza.thaumicpipes.common.core.interfaces.IThaumicPipe;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.MathHelper;
import thaumcraft.common.tiles.TileAlembic;
import thaumcraft.common.tiles.TileJarFillable;

public enum ConnectionType {
    DEFAULT,
    ALBEMIC(0.5F),
    JAR,
    PIPE;

    private float extensionSize = 0.0F;

    ConnectionType() {
    }

    ConnectionType(float extensionSize) {
        this.extensionSize = MathHelper.clamp_float(extensionSize, 0.0F, 1.0F);
    }

    public boolean isPipe() {
        return this == PIPE;
    }

    public boolean isAlbemic() {
        return this == ALBEMIC;
    }

    public boolean isJar() {
        return this == JAR;
    }

    public boolean isDefault() {
        return this == DEFAULT;
    }

    public float getExtensionSize() {
        return extensionSize;
    }

    public static ConnectionType getProperties(TileEntity tileEntity) {
        if (tileEntity instanceof IThaumicPipe)
            return PIPE;
        if (tileEntity instanceof TileJarFillable)
            return JAR;
        if (tileEntity instanceof TileAlembic)
            return ALBEMIC;
        return DEFAULT;
    }
}
