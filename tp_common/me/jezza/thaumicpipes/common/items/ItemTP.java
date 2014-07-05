package me.jezza.thaumicpipes.common.items;

import me.jezza.thaumicpipes.ThaumicPipes;
import me.jezza.thaumicpipes.common.lib.Reference;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.Entity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public abstract class ItemTP extends Item {

    public ItemTP(String name) {
        setName(name);
        setCreativeTab(ThaumicPipes.instance.thaumcraftCreativeTab);
        register(name);
    }

    private void setName(String name) {
        setUnlocalizedName(name);
        setTextureName(name);
    }

    private void register(String name) {
        GameRegistry.registerItem(this, name);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void registerIcons(IIconRegister iconRegister) {
        itemIcon = iconRegister.registerIcon(Reference.MOD_IDENTIFIER + getIconString());
    }
}
