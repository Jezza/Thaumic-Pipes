package me.jezza.thaumicpipes.common.core.multipart;

import java.util.LinkedList;
import java.util.List;

import me.jezza.thaumicpipes.common.interfaces.IThaumicPipe;

import codechicken.lib.vec.Cuboid6;
import codechicken.multipart.JCuboidPart;
import codechicken.multipart.JNormalOcclusion;
import codechicken.multipart.NormalOcclusionTest;
import codechicken.multipart.TMultiPart;

public class CuboidOcclusionTest extends JCuboidPart implements JNormalOcclusion {
    List<Cuboid6> occlusions = new LinkedList();
    Cuboid6 bounds;

    public CuboidOcclusionTest(Cuboid6 cuboid) {
        this.bounds = cuboid;
        this.occlusions.add(this.bounds);
    }

    public String getType() {
        return "-----";
    }

    public Cuboid6 getBounds() {
        return this.bounds;
    }

    public Iterable<Cuboid6> getOcclusionBoxes() {
        return this.occlusions;
    }

    public boolean occlusionTest(TMultiPart npart) {
        return (npart instanceof IThaumicPipe) ? true : NormalOcclusionTest.apply(this, npart);
    }
}