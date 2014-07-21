package me.jezza.thaumicpipes.common.multipart.pipe;

import me.jezza.thaumicpipes.client.IPartRenderer;
import me.jezza.thaumicpipes.common.core.utils.CoordSet;
import me.jezza.thaumicpipes.common.grid.interfaces.INetworkHandler;
import me.jezza.thaumicpipes.common.multipart.MultiPartAbstract;
import me.jezza.thaumicpipes.common.multipart.OcclusionPart;
import me.jezza.thaumicpipes.common.multipart.OcclusionPartTester;
import me.jezza.thaumicpipes.common.transport.connection.NodeState;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.util.ForgeDirection;
import codechicken.lib.data.MCDataInput;
import codechicken.lib.data.MCDataOutput;
import codechicken.lib.vec.Vector3;
import codechicken.multipart.INeighborTileChange;
import codechicken.multipart.TMultiPart;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public abstract class PipePartAbstract extends MultiPartAbstract implements INeighborTileChange {

    private boolean shouldUpdate = false;
    private TileEntity[] tileCache;

    public OcclusionPartTester occlusionTester;
    public NodeState nodeState;

    public PipePartAbstract() {
        tileCache = new TileEntity[6];
        occlusionTester = new OcclusionPartTester();
    }

    public CoordSet getCoordSet() {
        return new CoordSet(tile());
    }

    public TileEntity[] getTileCache() {
        return tileCache;
    }

    @Override
    public void update() {
        if (shouldUpdate) {
            shouldUpdate = false;
            updateStates();
        }
    }

    public void updateStates() {
        int index = 0;
        for (ForgeDirection direction : ForgeDirection.VALID_DIRECTIONS)
            tileCache[index++] = getCoordSet().addForgeDirection(direction).getTileEntity(world());
    }

    @Override
    public void onNeighborChanged() {
        shouldUpdate = true;
        if (world().isRemote)
            return;

        sendDescUpdate();
    }

    @Override
    public void writeDesc(MCDataOutput packet) {
        super.writeDesc(packet);
        packet.writeBoolean(true);
    }

    @Override
    public void readDesc(MCDataInput packet) {
        super.readDesc(packet);
        shouldUpdate = packet.readBoolean();
    }

    @Override
    public void onPartChanged(TMultiPart part) {
        shouldUpdate = true;
    }

    @Override
    public void onNeighborTileChanged(int arg0, boolean arg1) {
        shouldUpdate = true;
    }

    @Override
    public boolean weakTileChanges() {
        return false;
    }

    public boolean passOcclusionTest(OcclusionPart part) {
        return tile().occlusionTest(tile().partList(), part);
    }

    public boolean theyPassOcculsionTest(PipePartAbstract them, OcclusionPart part) {
        return them.tile().occlusionTest(them.tile().partList(), part);
    }

    public boolean bothPassOcclusionTest(PipePartAbstract them, OcclusionPart part, OcclusionPart oppositePart) {
        return theyPassOcculsionTest(them, oppositePart) && passOcclusionTest(part);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void renderDynamic(Vector3 pos, float frame, int pass) {
        if (pass == 0)
            getRenderer().renderAt(this, pos.x, pos.y, pos.z, frame);
    }

    public abstract INetworkHandler getNetworkHandler();

    @SideOnly(Side.CLIENT)
    public abstract IPartRenderer getRenderer();
}
