package me.jezza.thaumicpipes.common.blocks;

import me.jezza.thaumicpipes.ThaumicPipes;
import me.jezza.thaumicpipes.common.lib.Reference;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public abstract class BlockTP extends Block {

    public BlockTP(Material material, String name) {
        super(material);
        setCreativeTab(ThaumicPipes.instance.thaumcraftCreativeTab);
        setBlockName(name);
        register(name);
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
    @SideOnly(Side.CLIENT)
    public void registerBlockIcons(IIconRegister iconRegister) {
        blockIcon = iconRegister.registerIcon(Reference.MOD_IDENTIFIER + getUnlocalizedName().replace("tile.", ""));
    }

    public abstract TileEntity getTileEntity();

    public abstract boolean renderWithModel();
}
