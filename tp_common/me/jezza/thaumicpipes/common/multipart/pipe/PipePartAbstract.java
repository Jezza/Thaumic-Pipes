package me.jezza.thaumicpipes.common.multipart.pipe;

import java.util.ArrayList;
import java.util.HashSet;

import net.minecraft.tileentity.TileEntity;
import me.jezza.thaumicpipes.client.core.NodeState;
import me.jezza.thaumicpipes.common.core.utils.CoordSet;
import me.jezza.thaumicpipes.common.interfaces.IPartRenderer;
import me.jezza.thaumicpipes.common.multipart.MultiPartAbstract;
import me.jezza.thaumicpipes.common.multipart.OcclusionPart;
import me.jezza.thaumicpipes.common.multipart.OcclusionPartTester;
import me.jezza.thaumicpipes.common.transport.ArmState;
import me.jezza.thaumicpipes.common.transport.ArmStateHandler;
import codechicken.lib.data.MCDataInput;
import codechicken.lib.data.MCDataOutput;
import codechicken.lib.vec.Vector3;
import codechicken.multipart.INeighborTileChange;
import codechicken.multipart.TMultiPart;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public abstract class PipePartAbstract extends MultiPartAbstract implements INeighborTileChange {

    private boolean shouldUpdate = false;

    public OcclusionPartTester occlusionTester;
    public ArmStateHandler armStateHandler;
    public NodeState nodeState;

    public PipePartAbstract() {
        occlusionTester = new OcclusionPartTester();
        armStateHandler = new ArmStateHandler();
    }

    public CoordSet getCoordSet() {
        return new CoordSet(tile());
    }

    @Override
    public void update() {
        if (shouldUpdate) {
            shouldUpdate = false;
            updateStates();
        }
    }

    public ArrayList<TileEntity> getConnectableTiles(HashSet<CoordSet> coordSets) {
        if (coordSets == null)
            coordSets = new HashSet<CoordSet>();
        ArrayList<TileEntity> tileList = new ArrayList<TileEntity>();

        for (ArmState state : armStateHandler.getArmStateArray()) {
            if (state == null || !state.isValid())
                continue;
            CoordSet tempSet = state.getCoordSet();
            if (coordSets.contains(tempSet))
                continue;
            coordSets.add(tempSet);
            tileList.add(state.getTileEntity());
        }

        return tileList;
    }

    @Override
    public void onNeighborChanged() {
        super.onNeighborChanged();

        updateStates();
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
        updateStates();
    }

    @Override
    public void onNeighborTileChanged(int arg0, boolean arg1) {
        updateStates();
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

    @SideOnly(Side.CLIENT)
    public abstract IPartRenderer getRenderer();

    public abstract void updateStates();
}
