package me.jezza.thaumicpipes.common.transport.connection;

import me.jezza.thaumicpipes.api.registry.ConnectionRegistry;
import me.jezza.thaumicpipes.api.registry.RegistryEntry;
import me.jezza.thaumicpipes.common.core.utils.CoordSet;
import me.jezza.thaumicpipes.common.multipart.OcclusionPart;
import me.jezza.thaumicpipes.common.multipart.pipe.PipeProperties;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.util.ForgeDirection;
import codechicken.lib.vec.Cuboid6;

public class ArmState {
    private boolean isValidConnection = true;

    private TileEntity tileEntity;
    private ForgeDirection direction;
    private CoordSet coordSet;
    private RegistryEntry entry;

    private RenderType renderOverride = RenderType.DEFAULT;

    public ArmState(ForgeDirection direction, TileEntity tileEntity, boolean isValidConnection) {
        this.direction = direction;
        this.tileEntity = tileEntity;
        this.isValidConnection = isValidConnection;
        if (isValidConnection) {
            this.coordSet = new CoordSet(tileEntity);
            entry = ConnectionRegistry.getRegistryEntry(tileEntity);
            renderOverride = RenderType.getType(tileEntity);
        }
    }

    public RenderType getRenderOverride() {
        return renderOverride;
    }

    public boolean isValid() {
        return isValidConnection;
    }

    public boolean isPipe() {
        return renderOverride.isPipe();
    }

    public RegistryEntry getEntry() {
        return entry;
    }

    public ForgeDirection getDirection() {
        return direction;
    }

    public TileEntity getTileEntity() {
        return tileEntity;
    }

    public TransportState getTransportState() {
        return new TransportState(tileEntity, direction);
    }

    public CoordSet getCoordSet() {
        return coordSet.copy();
    }

    public boolean isOppositeSide(ArmState secondState) {
        return direction.getOpposite().equals(secondState.getDirection());
    }

    public Cuboid6 getOcclusionBox() {
        return PipeProperties.ARM_STATE_OCCLUSION_BOXES[direction.ordinal()];
    }

    public OcclusionPart createAsPart() {
        return new OcclusionPart(getOcclusionBox());
    }
}