package me.jezza.thaumicpipes.common.transport;

import codechicken.lib.vec.Cuboid6;
import me.jezza.thaumicpipes.common.multipart.OcclusionPart;
import me.jezza.thaumicpipes.common.multipart.pipe.PipeProperties;
import me.jezza.thaumicpipes.common.transport.connection.ConnectionType;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.util.ForgeDirection;

public class ArmState {
    private ForgeDirection direction;
    private ConnectionType connectionType;
    private boolean priority;
    private int position = 0;

    public ArmState(ForgeDirection direction, TileEntity tileEntity, boolean canConnect, boolean priority, int position) {
        this.direction = direction;
        this.priority = priority;
        this.position = position;
        connectionType = canConnect ? ConnectionType.getConnectionType(tileEntity) : ConnectionType.UNKNOWN;
    }

    public int getPosition() {
        return position;
    }

    public boolean isValid() {
        return connectionType.isValid();
    }

    public ConnectionType getType() {
        return connectionType;
    }

    public ForgeDirection getDirection() {
        return direction;
    }

    public boolean isPriority() {
        return isValid() && priority;
    }

    public Cuboid6 getOcclusionBox() {
        return PipeProperties.ARM_STATE_OCCLUSION_BOXES[direction.ordinal()];
    }

    public OcclusionPart createAsPart() {
        return new OcclusionPart(getOcclusionBox());
    }
}