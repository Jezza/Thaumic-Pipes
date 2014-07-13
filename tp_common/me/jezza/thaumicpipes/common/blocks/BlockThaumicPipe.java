package me.jezza.thaumicpipes.common.blocks;

import java.util.List;

import me.jezza.thaumicpipes.common.tileentity.TileThaumicPipe;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockThaumicPipe extends BlockTP {

    private static final float MIN = 0.3125F;
    private static final float MAX = 0.6875F;

    public BlockThaumicPipe(Material material, String name) {
        super(material, name);
        setHardness(3F);
        setCreativeTab(null);
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

    @Override
    public TileEntity getTileEntity() {
        return new TileThaumicPipe();
    }

    @Override
    public boolean renderWithModel() {
        return true;
    }
}
