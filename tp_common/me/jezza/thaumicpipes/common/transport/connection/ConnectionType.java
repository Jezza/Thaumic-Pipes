package me.jezza.thaumicpipes.common.transport.connection;

import me.jezza.thaumicpipes.common.core.interfaces.IThaumicPipe;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.MathHelper;
import thaumcraft.api.aspects.IAspectContainer;
import thaumcraft.common.tiles.TileAlembic;
import thaumcraft.common.tiles.TileJarFillable;
import thaumcraft.common.tiles.TileThaumatorium;
import thaumcraft.common.tiles.TileThaumatoriumTop;

public enum ConnectionType {
    DEFAULT,
    ALBEMIC(0.5F),
    JAR,
    CONSTRUCT,
    ASPECT_CONTAINER,
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
        if (tileEntity instanceof TileThaumatorium || tileEntity instanceof TileThaumatoriumTop)
            return CONSTRUCT;
        if (tileEntity instanceof IAspectContainer)
            return ASPECT_CONTAINER;
        return DEFAULT;
    }
}
