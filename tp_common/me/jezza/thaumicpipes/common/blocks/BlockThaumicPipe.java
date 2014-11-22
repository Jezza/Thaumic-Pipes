package me.jezza.thaumicpipes.common.blocks;

import me.jezza.oc.common.blocks.BlockAbstract;
import me.jezza.thaumicpipes.common.lib.CoreProperties;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import java.util.List;

public class BlockThaumicPipe extends BlockAbstract {

    private static final float MIN = 0.3125F;
    private static final float MAX = 0.6875F;

    public BlockThaumicPipe(Material material, String name) {
        super(material, name);
        setHardness(3F);
        setCreativeTab(null);
    }

    @Override
    public String getModIdentifier() {
        return CoreProperties.MOD_IDENTIFIER;
    }

    @Override
    public void setBlockBoundsBasedOnState(IBlockAccess world, int x, int y, int z) {
        setBlockBounds(MIN, MIN, MIN, MAX, MAX, MAX);
        super.setBlockBoundsBasedOnState(world, x, y, z);
    }

    @Override
    public void addCollisionBoxesToList(World world, int x, int y, int z, AxisAlignedBB playerBoundingBox, List list, Entity entity) {
        setBlockBounds(MIN, MIN, MIN, MAX, MAX, MAX);
        super.addCollisionBoxesToList(world, x, y, z, playerBoundingBox, list, entity);
    }

}
