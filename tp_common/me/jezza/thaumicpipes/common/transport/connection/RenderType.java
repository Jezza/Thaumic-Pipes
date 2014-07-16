package me.jezza.thaumicpipes.common.transport.connection;

import me.jezza.thaumicpipes.api.interfaces.IThaumicPipe;
import net.minecraft.tileentity.TileEntity;
import thaumcraft.common.tiles.TileJarFillable;

public enum RenderType {
    DEFAULT, JAR, PIPE;

    public boolean isPipe() {
        return this == PIPE;
    }

    public boolean isJar() {
        return this == JAR;
    }

    public boolean isDefault() {
        return this == DEFAULT;
    }

    public static RenderType getType(TileEntity tileEntity) {
        if (tileEntity instanceof IThaumicPipe)
            return PIPE;
        if (tileEntity instanceof TileJarFillable)
            return JAR;
        return DEFAULT;
    }
}
