package me.jezza.thaumicpipes.common.transport.connection;

import codechicken.lib.vec.Cuboid6;
import me.jezza.thaumicpipes.common.core.PipeProperties;
import me.jezza.thaumicpipes.common.core.interfaces.IOcclusionPart;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.util.ForgeDirection;

public class ArmState implements IOcclusionPart, Cloneable {

    private boolean isValidConnection = true;
    private ForgeDirection direction = ForgeDirection.UNKNOWN;
    private ConnectionType connectionType = ConnectionType.DEFAULT;

    public ArmState() {
    }

    public ArmState(ForgeDirection direction, TileEntity tileEntity, boolean isValidConnection) {
        setFields(direction, tileEntity, isValidConnection);
    }

    public ConnectionType getConnectionType() {
        return connectionType;
    }

    public ForgeDirection getDirection() {
        return direction;
    }

    public ArmState setFields(ForgeDirection direction, TileEntity tileEntity, boolean isValidConnection) {
        this.direction = direction;
        connectionType = ConnectionType.getProperties(tileEntity);
        this.isValidConnection = isValidConnection;
        return this;
    }

    public boolean isPipe() {
        return connectionType.isPipe();
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
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