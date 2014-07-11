package me.jezza.thaumicpipes.common.multipart.pipe;

import me.jezza.thaumicpipes.client.core.NodeState;
import me.jezza.thaumicpipes.common.core.utils.CoordSet;
import me.jezza.thaumicpipes.common.interfaces.IPartRenderer;
import me.jezza.thaumicpipes.common.multipart.MultiPartAbstract;
import me.jezza.thaumicpipes.common.multipart.OcclusionPart;
import me.jezza.thaumicpipes.common.multipart.OcclusionPartTester;
import me.jezza.thaumicpipes.common.transport.ArmStateHandler;
import codechicken.lib.vec.Cuboid6;
import codechicken.lib.vec.Vector3;
import codechicken.multipart.INeighborTileChange;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public abstract class PipePartAbstract extends MultiPartAbstract implements INeighborTileChange {

    public OcclusionPartTester occlusionTester;
    public ArmStateHandler armStateHandler;
    public NodeState nodeState;

    private Cuboid6 boundingBox = PipeProperties.callDefault();

    public PipePartAbstract() {
        occlusionTester = new OcclusionPartTester();
        armStateHandler = new ArmStateHandler();
    }

    public CoordSet getCoordSet() {
        return new CoordSet(tile());
    }

    @Override
    public Cuboid6 getBounds() {
        return boundingBox;
    }

    public void updateBoundingState() {
        Cuboid6 mainCuboid6 = null;
        for (Cuboid6 cuboid6 : getAllOcclusionBoxes()) {
            if (mainCuboid6 == null)
                mainCuboid6 = cuboid6.copy();
            else
                mainCuboid6.enclose(cuboid6);
        }

        boundingBox = mainCuboid6 != null ? mainCuboid6 : PipeProperties.callDefault();
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
        getRenderer().renderAt(this, pos.x, pos.y, pos.z, frame);
    }

    public abstract IPartRenderer getRenderer();

    public abstract Iterable<Cuboid6> getAllOcclusionBoxes();
}
