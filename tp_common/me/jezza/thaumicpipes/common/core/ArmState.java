package me.jezza.thaumicpipes.common.core;

import me.jezza.thaumicpipes.common.core.utils.ThaumicHelper;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.util.ForgeDirection;

public class ArmState {
    private ForgeDirection direction;
    private ConnectionState connectionState;
    private boolean priority;
    private int position = 0;

    public ArmState(ForgeDirection direction, TileEntity tileEntity, boolean canConnect, boolean priority, int position) {
        this.direction = direction;
        this.priority = priority;
        this.position = position;
        connectionState = ConnectionState.getConnectionState(tileEntity, canConnect);
    }

    public int getPosition() {
        return position;
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
        return isValid() && priority;
    }
}