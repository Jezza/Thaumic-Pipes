package me.jezza.thaumicpipes.common.core;

import me.jezza.thaumicpipes.common.core.utils.ThaumicHelper;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.util.ForgeDirection;

public class ArmState {
    private ForgeDirection direction;
    private ConnectionState connectionState;
    private boolean priority;

    public ArmState(ForgeDirection direction, TileEntity tileEntity, boolean canConnect, boolean priority) {
        this.direction = direction;
        this.priority = priority;
        connectionState = ConnectionState.getConnectionState(tileEntity, canConnect);
    }

    public boolean isValid() {
        return connectionState.getType().isValid();
    }

    public ConnectionState getConnectionState() {
        return connectionState;
    }

    public ForgeDirection getDirection() {
        return direction;
    }

    public boolean isPriority() {
        return priority;
    }
}