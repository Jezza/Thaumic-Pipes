package me.jezza.thaumicpipes.common.multipart.occlusion;

import codechicken.lib.vec.Cuboid6;
import me.jezza.thaumicpipes.common.core.interfaces.IOcclusionPart;

import java.util.LinkedList;
import java.util.List;

/**
 * Used to handle/calculate all the parts counted as the part.
 * EG, all armStates and nodeState.
 * <p/>
 * An occlusion collection class.
 */
public class OcclusionPartTester {
    private List<Cuboid6> occlusionBoxes;

    public OcclusionPartTester() {
        occlusionBoxes = new LinkedList<>();
    }

    public Iterable<Cuboid6> update(IOcclusionPart... parts) {
        occlusionBoxes.clear();
        for (int i = 0; i < parts.length; i++) {
            IOcclusionPart part = parts[i];
            if (part.isPartValid())
                occlusionBoxes.add(part.getOcclusionBox());
        }

        return occlusionBoxes;
    }

    public List<Cuboid6> getOcclusionBoxes() {
        return occlusionBoxes;
    }
}
