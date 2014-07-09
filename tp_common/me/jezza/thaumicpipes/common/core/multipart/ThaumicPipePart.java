package me.jezza.thaumicpipes.common.core.multipart;

import java.util.ArrayList;

import me.jezza.thaumicpipes.common.ModBlocks;
import me.jezza.thaumicpipes.common.tileentity.TileThaumicPipe;
import me.jezza.thaumicpipes.common.transport.ArmState;
import net.minecraft.block.Block;
import net.minecraft.tileentity.TileEntity;
import codechicken.lib.vec.Cuboid6;

public class ThaumicPipePart extends MultiPartAbstract {

    private ArmState[] armStates;

    private static float minX, minY, minZ;
    private static float maxX, maxY, maxZ;

    private static final float MIN = 0.3125F;
    private static final float MAX = 0.6875F;

    public ThaumicPipePart(TileThaumicPipe pipe) {
        armStates = pipe.getArmStateArray();
    }

    public ThaumicPipePart() {
    }

    @Override
    public void onNeighborChanged() {
        TileEntity tileEntity = tile();
        if (tileEntity instanceof TileThaumicPipe) {
            armStates = ((TileThaumicPipe) tileEntity).getArmStateArray();
            sendDescUpdate();
            tile().notifyPartChange(this);
            tile().markDirty();
        }
    }

    @Override
    public Cuboid6 getBounds() {
        minX = minY = minZ = MIN;
        maxX = maxY = maxZ = MAX;

        if (armStates == null)
            return new Cuboid6(minX, minY, minZ, maxX, maxY, maxZ);

        for (ArmState currentState : armStates) {
            if (currentState == null)
                continue;

            if (currentState.isValid()) {
                switch (currentState.getDirection()) {
                    case DOWN:
                        minY = 0.0F;
                        break;
                    case UP:
                        maxY = 1.0F;
                        break;
                    case NORTH:
                        minZ = 0.0F;
                        break;
                    case SOUTH:
                        maxZ = 1.0F;
                        break;
                    case WEST:
                        minX = 0.0F;
                        break;
                    case EAST:
                        maxX = 1.0F;
                        break;
                    default:
                        break;
                }
            }
        }
        return new Cuboid6(minX, minY, minZ, maxX, maxY, maxZ);
    }

    @Override
    public Iterable<Cuboid6> getOcclusionBoxes() {
        ArrayList<Cuboid6> occlusionBoxes = new ArrayList<Cuboid6>();

        minX = minY = minZ = MIN;
        maxX = maxY = maxZ = MAX;

        occlusionBoxes.add(new Cuboid6(minX, minY, minZ, maxX, maxY, maxZ));

        if (armStates == null)
            return occlusionBoxes;

        for (ArmState currentState : armStates) {
            if (currentState == null)
                continue;

            if (currentState.isValid()) {
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
                        maxZ = 0.0F;
                        maxZ = -0.5F;
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

                minX = minY = minZ = MIN;
                maxX = maxY = maxZ = MAX;
                occlusionBoxes.add(new Cuboid6(minX, minY, minZ, maxX, maxY, maxZ));
            }
        }

        return occlusionBoxes;
    }

    @Override
    public String getType() {
        return "tp_thaumicPipe";
    }

    @Override
    public Block getBlock() {
        return ModBlocks.thaumicPipe;
    }

}
