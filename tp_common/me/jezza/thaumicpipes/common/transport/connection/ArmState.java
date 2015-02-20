package me.jezza.thaumicpipes.common.transport.connection;

import codechicken.lib.vec.Cuboid6;
import me.jezza.thaumicpipes.common.core.PipeProperties;
import me.jezza.thaumicpipes.common.core.interfaces.IOcclusionPart;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.util.ForgeDirection;

public class ArmState implements IOcclusionPart {

    public final ForgeDirection direction;

    private boolean isValidConnection = false;
    private int connectionType = ConnectionType.DEFAULT | ConnectionType.INVALID;
    private boolean isPipe = false;

    public ArmState(ForgeDirection direction) {
        this.direction = direction;
    }

    public ArmState update(ForgeDirection direction, TileEntity tileEntity, boolean isValidConnection) {
        this.isValidConnection = isValidConnection;
        if (!isValidConnection) {
            connectionType = ConnectionType.DEFAULT_INVALID;
            return this;
        }
        connectionType = ConnectionType.getProperties(direction, tileEntity);
        this.isValidConnection = !ConnectionType.isInvalid(connectionType);
        if (this.isValidConnection)
            isPipe = ConnectionType.isPipe(connectionType);
        return this;
    }

    public int getConnectionType() {
        return connectionType;
    }

    public boolean isPipe() {
        return isPipe;
    }

    @Override
    public boolean isPartValid() {
        return isValidConnection;
    }

    @Override
    public Cuboid6 getOcclusionBox() {
        return PipeProperties.ARM_STATE_OCCLUSION_BOXES[direction.ordinal()];
    }
}