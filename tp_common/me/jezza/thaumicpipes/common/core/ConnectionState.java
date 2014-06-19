package me.jezza.thaumicpipes.common.core;

import me.jezza.thaumicpipes.common.core.utils.ThaumicHelper;
import net.minecraft.tileentity.TileEntity;

public class ConnectionState {
    private ConnectionType type = ConnectionType.UNKNOWN;
    private float extensionSize = 0.0F;

    private ConnectionState(ConnectionType type) {
        this.type = type;
    }

    public ConnectionType getType() {
        return type;
    }

    public float getExtensionSize() {
        return extensionSize;
    }

    public static ConnectionState getConnectionState(TileEntity tileEntity, boolean canConnect) {
        if (!canConnect)
            return new ConnectionState(ConnectionType.UNKNOWN);

        if (ThaumicHelper.isPipe(tileEntity))
            return new ConnectionState(ConnectionType.PIPE);

        if (ThaumicHelper.isJar(tileEntity))
            return new ConnectionState(ConnectionType.JAR);

        if (ThaumicHelper.isContainer(tileEntity))
            return new ConnectionState(ConnectionType.CONTAINER);

        if (ThaumicHelper.isAlchemicalConstruct(tileEntity))
            return new ConnectionState(ConnectionType.CONSTRUCT);

        return new ConnectionState(ConnectionType.UNKNOWN);
    }
}