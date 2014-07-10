package me.jezza.thaumicpipes.common.blocks;

import java.util.List;

import me.jezza.thaumicpipes.ThaumicPipes;
import me.jezza.thaumicpipes.common.interfaces.IBlockInteract;
import me.jezza.thaumicpipes.common.interfaces.IBlockNotifier;
import me.jezza.thaumicpipes.common.lib.Reference;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.Explosion;
import net.minecraft.world.World;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public abstract class BlockTP extends Block {

    public BlockTP(Material material, String name) {
        super(material);
        setCreativeTab(ThaumicPipes.creativeTab);
        setBlockName(name);
        setName(name);
        register(name);
    }

    private void setName(String name) {
        setBlockName(name);
        setBlockTextureName(name);
    }

    protected void register(String name) {
        GameRegistry.registerBlock(this, name);
    }

    @Override
    public boolean renderAsNormalBlock() {
        return !renderWithModel();
    }

    @Override
    public boolean isOpaqueCube() {
        return !renderWithModel();
    }

    @Override
    public int getRenderType() {
        return renderWithModel() ? -1 : super.getRenderType();
    }

    @Override
    public TileEntity createTileEntity(World world, int metadata) {
        return getTileEntity();
    }

    @Override
    public boolean hasTileEntity(int metadata) {
        return getTileEntity() == null ? false : true;
    }

    @Override
    public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int side, float hitVecX, float hitVecY, float hitVecZ) {
        TileEntity tileEntity = world.getTileEntity(x, y, z);
        if (tileEntity instanceof IBlockInteract)
            return ((IBlockInteract) tileEntity).onActivated(world, x, y, z, player, side, hitVecX, hitVecY, hitVecZ);
        return false;
    }

    @Override
    public void onBlockPlacedBy(World world, int x, int y, int z, EntityLivingBase entityLiving, ItemStack itemStack) {
        TileEntity tileEntity = world.getTileEntity(x, y, z);
        if (tileEntity instanceof IBlockNotifier)
            ((IBlockNotifier) tileEntity).onBlockPlaced(entityLiving, itemStack);
    }

    @Override
    public void onBlockExploded(World world, int x, int y, int z, Explosion explosion) {
        onBlockRemoval(world, x, y, z);
        super.onBlockExploded(world, x, y, z, explosion);
    }

    @Override
    public boolean removedByPlayer(World world, EntityPlayer player, int x, int y, int z) {
        onBlockRemoval(world, x, y, z);
        return super.removedByPlayer(world, player, x, y, z);
    }

    public void onBlockRemoval(World world, int x, int y, int z) {
        TileEntity tileEntity = world.getTileEntity(x, y, z);
        if (tileEntity instanceof IBlockNotifier)
            ((IBlockNotifier) tileEntity).onBlockDestroyed();
    }

    @Override
    public void addCollisionBoxesToList(World world, int x, int y, int z, AxisAlignedBB entityBoundingBox, List list, Entity entity) {
        AxisAlignedBB axisAligned = getCollisionBoundingBoxFromPool(world, x, y, z);
        if (axisAligned != null && entityBoundingBox.intersectsWith(axisAligned))
            list.add(axisAligned);
    }

    @Override
    public AxisAlignedBB getCollisionBoundingBoxFromPool(World world, int x, int y, int z) {
        return AxisAlignedBB.getBoundingBox(x + this.minX, y + this.minY, z + this.minZ, x + this.maxX, y + this.maxY, z + this.maxZ);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void registerBlockIcons(IIconRegister iconRegister) {
        blockIcon = iconRegister.registerIcon(Reference.MOD_IDENTIFIER + getTextureName());
    }

    public abstract TileEntity getTileEntity();

    public abstract boolean renderWithModel();
}
