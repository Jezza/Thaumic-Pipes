package me.jezza.thaumicpipes.common.multipart.pipe;

import codechicken.lib.vec.Cuboid6;

public class PipeProperties {

    public static final float MIN = 0.340F;
    public static final float MAX = 0.660F;

    public static final Cuboid6[] ARM_STATE_OCCLUSION_BOXES = new Cuboid6[7];

    static {
        // Arms
        ARM_STATE_OCCLUSION_BOXES[0] = new Cuboid6(MIN, 0.0F, MIN, MAX, MAX, MAX);
        ARM_STATE_OCCLUSION_BOXES[1] = new Cuboid6(MIN, MIN, MIN, MAX, 1.0F, MAX);
        ARM_STATE_OCCLUSION_BOXES[2] = new Cuboid6(MIN, MIN, 0.0F, MAX, MAX, MAX);
        ARM_STATE_OCCLUSION_BOXES[3] = new Cuboid6(MIN, MIN, MIN, MAX, MAX, 1.0F);
        ARM_STATE_OCCLUSION_BOXES[4] = new Cuboid6(0.0F, MIN, MIN, MAX, MAX, MAX);
        ARM_STATE_OCCLUSION_BOXES[5] = new Cuboid6(MIN, MIN, MIN, 1.0F, MAX, MAX);

        // Main node
        ARM_STATE_OCCLUSION_BOXES[6] = new Cuboid6(MIN, MIN, MIN, MAX, MAX, MAX);
    }

    public static Cuboid6 callDefault() {
        return ARM_STATE_OCCLUSION_BOXES[6];
    }

}
