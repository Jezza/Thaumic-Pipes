package me.jezza.thaumicpipes.common.transport;

import me.jezza.thaumicpipes.common.core.utils.CoordSet;
import me.jezza.thaumicpipes.common.multipart.OcclusionPart;
import me.jezza.thaumicpipes.common.multipart.pipe.PipeProperties;
import me.jezza.thaumicpipes.common.transport.connection.ConnectionType;
import me.jezza.thaumicpipes.common.transport.connection.TransportState;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.util.ForgeDirection;
import codechicken.lib.vec.Cuboid6;

public class ArmState {
    private TileEntity tileEntity;
    private ForgeDirection direction;
    private ConnectionType connectionType;
    private CoordSet coordSet;
    private boolean priority;

    public ArmState(ForgeDirection direction, TileEntity tileEntity, boolean canConnect, boolean priority) {
        this.direction = direction;
        this.priority = priority;
        this.tileEntity = tileEntity;
        if (canConnect) {
            coordSet = new CoordSet(tileEntity);
            connectionType = ConnectionType.getConnectionType(tileEntity);
        } else {
            coordSet = new CoordSet(0, 0, 0);
            connectionType = ConnectionType.UNKNOWN;
        }
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

    public TransportState getTransportState() {
        return new TransportState(tileEntity).setDirection(direction);
    }

    public CoordSet getCoordSet() {
        return coordSet.copy();
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