package me.jezza.thaumicpipes.common.core.multipart.pipe;

import me.jezza.thaumicpipes.common.core.multipart.CuboidOcclusionTest;
import me.jezza.thaumicpipes.common.core.multipart.MultiPartAbstract;
import me.jezza.thaumicpipes.common.core.multipart.OcclusionPartTest;
import me.jezza.thaumicpipes.common.core.utils.CoordSet;
import me.jezza.thaumicpipes.common.transport.ArmState;
import codechicken.lib.vec.Cuboid6;
import codechicken.multipart.INeighborTileChange;

public abstract class PipePartAbstract extends MultiPartAbstract implements INeighborTileChange {

    public OcclusionPartTest occlusionTester;

    public PipePartAbstract() {
        occlusionTester = new OcclusionPartTest();
    }

    public CoordSet getCoordSet() {
        return new CoordSet(tile());
    }

    @Override
    public Cuboid6 getBounds() {
        Cuboid6 mainCuboid6 = null;
        for (Cuboid6 cuboid6 : getOcclusionBoxes()) {
            if (mainCuboid6 == null)
                mainCuboid6 = cuboid6;
            else
                mainCuboid6.enclose(cuboid6);
        }

        if (mainCuboid6 != null)
            return mainCuboid6;

        return OcclusionPartTest.callDefault();
    }

    public boolean passOcclusionTest(ArmState armState) {
        CuboidOcclusionTest test = OcclusionPartTest.createCuboidOcclusionTest(occlusionTester.getOcclusionBoxForArmState(armState));
        return tile().occlusionTest(tile().partList(), test);
    }

    public boolean theyPassOcculsionTest(PipePartAbstract them, ArmState armState) {
        CuboidOcclusionTest test = OcclusionPartTest.createCuboidOcclusionTest(them.occlusionTester.getOcclusionBoxForArmState(armState));
        return them.tile().occlusionTest(them.tile().partList(), test);
    }

    public boolean bothPassOcclusionTest(PipePartAbstract them, ArmState armState) {
        return theyPassOcculsionTest(them, armState) && passOcclusionTest(armState);
    }
}
