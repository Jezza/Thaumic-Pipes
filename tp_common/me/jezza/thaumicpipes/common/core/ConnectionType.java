package me.jezza.thaumicpipes.common.core;

import me.jezza.thaumicpipes.common.core.utils.ThaumicHelper;
import net.minecraft.tileentity.TileEntity;

public enum ConnectionType {
    CONTAINER(0.5F), JAR(0.1F), PIPE(0.0F), CONSTRUCT(0.0F), UNKNOWN(-1.0F);

    private float size = 0.0F;

    private ConnectionType(float size) {
        this.size = size;
    }

    public float getSize() {
        return size;
    }

    public boolean isValid() {
        return this != UNKNOWN;
    }

    public boolean isContainer() {
        return this == CONTAINER;
    }

    public boolean isJar() {
        return this == JAR;
    }

    public boolean isPipe() {
        return this == PIPE;
    }

    public boolean isBigNode() {
        return isJar() || isContainer();
    }

    public static ConnectionType getConnectionType(TileEntity tileEntity, boolean canConnect) {
        if (!canConnect)
            return ConnectionType.UNKNOWN;

        if (ThaumicHelper.isPipe(tileEntity))
            return ConnectionType.PIPE;

        if (ThaumicHelper.isJar(tileEntity))
            return ConnectionType.JAR;

        if (ThaumicHelper.isContainer(tileEntity))
            return ConnectionType.CONTAINER;

        if (ThaumicHelper.isAlchemicalConstruct(tileEntity))
            return ConnectionType.CONSTRUCT;

        return ConnectionType.UNKNOWN;
    }
}