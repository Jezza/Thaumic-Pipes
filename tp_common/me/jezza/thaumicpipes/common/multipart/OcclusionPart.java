package me.jezza.thaumicpipes.common.multipart;

import java.util.ArrayList;

import me.jezza.thaumicpipes.api.interfaces.IThaumicPipe;
import codechicken.lib.vec.Cuboid6;
import codechicken.multipart.JCuboidPart;
import codechicken.multipart.JNormalOcclusion;
import codechicken.multipart.NormalOcclusionTest;
import codechicken.multipart.TMultiPart;

public class OcclusionPart extends JCuboidPart implements JNormalOcclusion {
    ArrayList<Cuboid6> occlusions = new ArrayList<Cuboid6>();
    Cuboid6 bounds;

    public OcclusionPart(Cuboid6 cuboid) {
        bounds = cuboid;
        occlusions.add(bounds);
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