package me.jezza.thaumicpipes.common.items;

import ic2.api.tile.IWrenchable;
import net.minecraft.block.Block;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.MovingObjectPosition.MovingObjectType;
import net.minecraft.world.World;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import cofh.api.block.IDismantleable;
import cofh.util.BlockHelper;

public class ItemFocusWrench extends ItemFociTP {

    public ItemFocusWrench(String name) {
        super(name);
    }

    @Override
    public ItemStack onFocusRightClick(ItemStack itemStack, World world, EntityPlayer player, MovingObjectPosition mop) {
        if (mop == null || mop.typeOfHit != MovingObjectType.BLOCK || world.isRemote)
            return itemStack;

        int x = mop.blockX;
        int y = mop.blockY;
        int z = mop.blockZ;
        int side = mop.sideHit;

        Block block = world.getBlock(x, y, z);
        TileEntity tileEntity = world.getTileEntity(x, y, z);

        if (triggerDismantle(tileEntity, block, player, world, x, y, z))
            return itemStack;

        if (triggerWrenchable(tileEntity, player, world, x, y, z))
            return itemStack;

        return itemStack;
    }

    @Override
    public boolean onEntitySwing(EntityLivingBase livingBase, ItemStack stack) {
        World world = livingBase.worldObj;

        if (world.isRemote)
            return false;

        if (livingBase instanceof EntityPlayer) {
            EntityPlayer player = (EntityPlayer) livingBase;

            MovingObjectPosition mop = getMovingObjectPositionFromPlayer(world, player, true);
            if (mop == null || mop.typeOfHit != MovingObjectType.BLOCK)
                return false;

            int x = mop.blockX;
            int y = mop.blockY;
            int z = mop.blockZ;
            int side = mop.sideHit;

            Block block = world.getBlock(x, y, z);
            TileEntity tileEntity = world.getTileEntity(x, y, z);

            if (player.isSneaking())
                side = BlockHelper.getOppositeSide(side);

            if (triggerWrenchableRotate(tileEntity, player, side))
                return true;

            if (triggerRotatable(block, player, world, x, y, z))
                return true;
        }
        return false;
    }


    private boolean triggerDismantle(TileEntity tileEntity, Block block, EntityPlayer player, World world, int x, int y, int z) {
        if (tileEntity instanceof IDismantleable || block instanceof IDismantleable) {
            IDismantleable dismantleable = null;
            if (tileEntity instanceof IDismantleable)
                dismantleable = (IDismantleable) tileEntity;
            else if (block instanceof IDismantleable)
                dismantleable = (IDismantleable) block;

            if (dismantleable != null && dismantleable.canDismantle(player, world, x, y, z))
                dismantleable.dismantleBlock(player, world, x, y, z, !player.capabilities.isCreativeMode);

            return true;
        }
        return false;
    }

    private boolean triggerWrenchable(TileEntity tileEntity, EntityPlayer player, World world, int x, int y, int z) {
        if (tileEntity instanceof IWrenchable) {
            IWrenchable machine = (IWrenchable) tileEntity;
            if (machine.wrenchCanRemove(player)) {
                ItemStack dropStack = machine.getWrenchDrop(player);
                if (dropStack != null) {
                    world.setBlockToAir(x, y, z);
                    if (!world.isRemote) {
                        float f = 0.7F;
                        double x2 = world.rand.nextFloat() * f + (1.0F - f) * 0.5D;
                        double y2 = world.rand.nextFloat() * f + (1.0F - f) * 0.5D;
                        double z2 = world.rand.nextFloat() * f + (1.0F - f) * 0.5D;
                        EntityItem entity = new EntityItem(world, x + x2, y + y2, z + z2, dropStack);
                        entity.delayBeforeCanPickup = 0;
                        world.spawnEntityInWorld(entity);
                    }
                }
            }

            return true;
        }
        return false;
    }

    private boolean triggerWrenchableRotate(TileEntity tileEntity, EntityPlayer player, int side) {
        if (tileEntity instanceof IWrenchable) {
            IWrenchable machine = (IWrenchable) tileEntity;
            if (machine.wrenchCanSetFacing(player, side))
                machine.setFacing((short) side);
            return true;
        }
        return false;
    }

    private boolean triggerRotatable(Block block, EntityPlayer player, World world, int x, int y, int z) {
        if (BlockHelper.canRotate(block)) {
            if (player.isSneaking())
                world.setBlockMetadataWithNotify(x, y, z, BlockHelper.rotateVanillaBlockAlt(world, block, x, y, z), 3);
            else
                world.setBlockMetadataWithNotify(x, y, z, BlockHelper.rotateVanillaBlock(world, block, x, y, z), 3);
            return true;
        }
        return false;
    }

    @Override
    protected boolean hasOrnament() {
        return true;
    }

    @Override
    protected boolean hasDepth() {
        return true;
    }

    @Override
    public int getFocusColor() {
        return 0xFFFFFF;
    }

    @Override
    public AspectList getVisCost() {
        return new AspectList().add(Aspect.AIR, 0);
    }

    @Override
    public void onUsingFocusTick(ItemStack itemstack, EntityPlayer player, int count) {
    }

    @Override
    public String getSortingHelper(ItemStack itemstack) {
        return "WRENCH";
    }
}
