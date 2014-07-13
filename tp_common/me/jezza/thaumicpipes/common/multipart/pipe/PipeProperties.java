package me.jezza.thaumicpipes.common.multipart.pipe;

import codechicken.lib.vec.Cuboid6;

public class PipeProperties {

    public static final float MIN = 0.340F;
    public static final float MAX = 0.660F;

    public static final float PIPE_MIN = 0.41F;
    public static final float PIPE_MAX = 0.59F;

    public static final Cuboid6[] ARM_STATE_OCCLUSION_BOXES = new Cuboid6[6];
    public static final Cuboid6[] NODE_OCCLUSION_BOXES = new Cuboid6[6];

    static {
        ARM_STATE_OCCLUSION_BOXES[0] = new Cuboid6(PIPE_MIN, 0.0F, PIPE_MIN, PIPE_MAX, PIPE_MIN, PIPE_MAX);
        ARM_STATE_OCCLUSION_BOXES[1] = new Cuboid6(PIPE_MIN, PIPE_MAX, PIPE_MIN, PIPE_MAX, 1.0F, PIPE_MAX);
        ARM_STATE_OCCLUSION_BOXES[2] = new Cuboid6(PIPE_MIN, PIPE_MIN, 0.0F, PIPE_MAX, PIPE_MAX, PIPE_MIN);
        ARM_STATE_OCCLUSION_BOXES[3] = new Cuboid6(PIPE_MIN, PIPE_MIN, PIPE_MAX, PIPE_MAX, PIPE_MAX, 1.0F);
        ARM_STATE_OCCLUSION_BOXES[4] = new Cuboid6(0.0F, PIPE_MIN, PIPE_MIN, PIPE_MIN, PIPE_MAX, PIPE_MAX);
        ARM_STATE_OCCLUSION_BOXES[5] = new Cuboid6(PIPE_MAX, PIPE_MIN, PIPE_MIN, 1.0F, PIPE_MAX, PIPE_MAX);

        NODE_OCCLUSION_BOXES[0] = new Cuboid6(PIPE_MIN, MIN, PIPE_MIN, PIPE_MAX, MAX, PIPE_MAX);
        NODE_OCCLUSION_BOXES[1] = new Cuboid6(PIPE_MIN, PIPE_MIN, MIN, PIPE_MAX, PIPE_MAX, MAX);
        NODE_OCCLUSION_BOXES[2] = new Cuboid6(MIN, PIPE_MIN, PIPE_MIN, MAX, PIPE_MAX, PIPE_MAX);
    }

    public static Cuboid6 getNode() {
        return new Cuboid6(MIN, MIN, MIN, MAX, MAX, MAX);
    }

    public static Cuboid6 getSlimedNode(int direction) {
        switch (direction) {
            case 0:
            case 1:
                return NODE_OCCLUSION_BOXES[0];
            case 2:
            case 3:
                return NODE_OCCLUSION_BOXES[1];
            case 4:
            case 5:
                return NODE_OCCLUSION_BOXES[2];
            default:
                return getNode();
        }
    }
}
