package me.jezza.thaumicpipes.common.transport;

import me.jezza.thaumicpipes.api.interfaces.IThaumicPipe;
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

    private ArmState(ForgeDirection direction, TileEntity tileEntity, ConnectionType connectionType) {
        this.direction = direction;
        this.tileEntity = tileEntity;
        this.connectionType = connectionType;
        if (connectionType.isValid())
            this.coordSet = new CoordSet(tileEntity);
    }

    public static ArmState create(ForgeDirection direction, TileEntity tileEntity, IThaumicPipe pipe) {
        ConnectionType type = ConnectionType.UNKNOWN;

        if (pipe.canConnectTo(direction))
            type = ConnectionType.getConnectionType(tileEntity);

        return new ArmState(direction, tileEntity, type);
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

    public TileEntity getTileEntity() {
        return tileEntity;
    }

    public TransportState getTransportState() {
        return new TransportState(tileEntity, direction, connectionType);
    }

    public CoordSet getCoordSet() {
        return coordSet.copy();
    }

    public Cuboid6 getOcclusionBox() {
        return PipeProperties.ARM_STATE_OCCLUSION_BOXES[direction.ordinal()];
    }

    public OcclusionPart createAsPart() {
        return new OcclusionPart(getOcclusionBox());
    }
}