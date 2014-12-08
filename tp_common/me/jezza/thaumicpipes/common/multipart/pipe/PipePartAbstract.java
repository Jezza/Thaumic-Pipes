package me.jezza.thaumicpipes.common.multipart.pipe;

import codechicken.lib.data.MCDataInput;
import codechicken.lib.data.MCDataOutput;
import codechicken.lib.vec.Vector3;
import codechicken.multipart.INeighborTileChange;
import codechicken.multipart.TMultiPart;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import me.jezza.oc.common.utils.CoordSet;
import me.jezza.thaumicpipes.client.interfaces.IDynamicPartRenderer;
import me.jezza.thaumicpipes.client.interfaces.IStaticPartRenderer;
import me.jezza.thaumicpipes.common.core.interfaces.IOcclusionPart;
import me.jezza.thaumicpipes.common.multipart.core.MultiPartAbstract;
import me.jezza.thaumicpipes.common.multipart.occlusion.OcclusionPart;
import me.jezza.thaumicpipes.common.multipart.occlusion.OcclusionPartTester;
import me.jezza.thaumicpipes.common.transport.connection.NodeState;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

public abstract class PipePartAbstract extends MultiPartAbstract implements INeighborTileChange {

    private boolean isDynamicRenderer = false;
    private boolean shouldUpdate = false;
    private TileEntity[] tileCache;

    public OcclusionPartTester occlusionTester;
    public NodeState nodeState;

    public PipePartAbstract() {
        tileCache = new TileEntity[6];
        occlusionTester = new OcclusionPartTester();
    }

    public PipePartAbstract setDynamicRenderer() {
        isDynamicRenderer = true;
        return this;
    }

    public TileEntity[] getTileCache() {
        return tileCache;
    }

    @Override
    public void update() {
        if (shouldUpdate) {
            shouldUpdate = false;
            updateTileCache();
            updateConnections();
            updateOcclusions();
            updateNetwork();
        }
    }

    public void updateTileCache() {
        CoordSet coordSet = getCoordSet();
        World world = world();
        ForgeDirection[] directions = ForgeDirection.VALID_DIRECTIONS;
        for (int i = 0; i <= 5; i++)
            tileCache[i] = coordSet.getTileFromDirection(world, directions[i]);
    }

    public void updateOcclusions() {
        occlusionTester.update(getOcclusionParts());
    }

    public void updateConnections() {
    }

    public void updateNetwork() {
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

    public boolean theyPassOcclusionTest(PipePartAbstract them, OcclusionPart part) {
        return them.tile().occlusionTest(them.tile().partList(), part);
    }

    public boolean bothPassOcclusionTest(PipePartAbstract them, OcclusionPart part, OcclusionPart oppositePart) {
        return theyPassOcclusionTest(them, oppositePart) && passOcclusionTest(part);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void renderDynamic(Vector3 pos, float frame, int pass) {
        if (!isDynamicRenderer)
            return;
        if (pass == 0) {
            IDynamicPartRenderer renderer = getDynamicRenderer();
            if (renderer != null)
                renderer.renderAt(this, pos.x, pos.y, pos.z, frame);
        }
    }

    @Override
    public boolean renderStatic(Vector3 pos, int pass) {
        if (isDynamicRenderer)
            return false;
        if (pass == 0) {
            IStaticPartRenderer renderer = getStaticRenderer();
            if (renderer != null)
                return renderer.renderAt(this, Tessellator.instance);
        }
        return false;
    }

    @SideOnly(Side.CLIENT)
    public IStaticPartRenderer getStaticRenderer() {
        return null;
    }

    @SideOnly(Side.CLIENT)
    public IDynamicPartRenderer getDynamicRenderer() {
        return null;
    }

    public abstract IOcclusionPart[] getOcclusionParts();

}
