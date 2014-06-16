package me.jezza.thaumicpipes.common.items;

import me.jezza.thaumicpipes.ThaumicPipes;
import me.jezza.thaumicpipes.common.lib.Reference;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.item.Item;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public abstract class ItemTP extends Item {

    public ItemTP(String name) {
        setUnlocalizedName(name);
        setCreativeTab(ThaumicPipes.instance.thaumcraftCreativeTab);
        register(name);
    }

    private void register(String name) {
        GameRegistry.registerItem(this, name);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void registerIcons(IIconRegister iconRegister) {
        itemIcon = iconRegister.registerIcon(Reference.MOD_IDENTIFIER + this.getUnlocalizedName().replace("item.", ""));
    }
}
