package me.jezza.thaumicpipes.common.core.multipart;

import java.util.ArrayList;

import me.jezza.thaumicpipes.common.core.TPLogger;
import me.jezza.thaumicpipes.common.transport.ArmState;
import codechicken.lib.vec.Cuboid6;

public class OcclusionPartTest {

    public static float minX, minY, minZ;
    public static float maxX, maxY, maxZ;

    public static final float MIN = 0.3125F;
    public static final float MAX = 0.6875F;

    private ArrayList<Cuboid6> occlusionBoxes;

    public OcclusionPartTest() {
        occlusionBoxes = new ArrayList<Cuboid6>();
    }

    public Iterable<Cuboid6> fetchFromArmState(ArmState[] armStates) {
        if (armStates == null)
            return occlusionBoxes;
        prepCalculations();

        for (ArmState currentState : armStates) {
            if (currentState == null || !currentState.isValid())
                continue;
            occlusionBoxes.add(getOcclusionBoxForArmState(currentState));
        }

        return occlusionBoxes;
    }

    public Cuboid6 getOcclusionBoxForArmState(ArmState currentState) {
        resetBounds();

        if (currentState == null) {
            TPLogger.info("Returning default");
            return callDefault();
        }

        switch (currentState.getDirection()) {
            case DOWN:
                minY = 0.0F;
                maxY = 0.5F;
                break;
            case UP:
                minY = 0.5F;
                maxY = 1.0F;
                break;
            case NORTH:
                minZ = 0.0F;
                maxZ = 0.5F;
                break;
            case SOUTH:
                maxZ = 0.5F;
                maxZ = 1.0F;
                break;
            case WEST:
                minX = 0.0F;
                maxX = 0.5F;
                break;
            case EAST:
                minX = 0.5F;
                maxX = 1.0F;
                break;
            default:
                break;
        }

        return new Cuboid6(minX, minY, minZ, maxX, maxY, maxZ);
    }

    public static CuboidOcclusionTest createCuboidOcclusionTest(Cuboid6 cuboid) {
        return new CuboidOcclusionTest(cuboid);
    }

    private void prepCalculations() {
        resetBounds();
        occlusionBoxes.clear();
        occlusionBoxes.add(new Cuboid6(minX, minY, minZ, maxX, maxY, maxZ));
    }

    private void resetBounds() {
        minX = minY = minZ = MIN;
        maxX = maxY = maxZ = MAX;
    }

    public static Cuboid6 callDefault() {
        return new Cuboid6(MIN, MIN, MIN, MAX, MAX, MAX);
    }

}
