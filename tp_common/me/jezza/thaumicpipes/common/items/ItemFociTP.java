package me.jezza.thaumicpipes.common.items;

import java.util.List;

import javax.swing.Icon;

import me.jezza.thaumicpipes.common.lib.Reference;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.wands.IWandFocus;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public abstract class ItemFociTP extends ItemTP implements IWandFocus {

    private IIcon ornament, depth;

    public ItemFociTP(String name) {
        super(name);
        setMaxDamage(1);
        setNoRepair();
        setMaxStackSize(1);
    }

    protected boolean hasOrnament() {
        return false;
    }

    protected boolean hasDepth() {
        return false;
    }

    @Override
    public boolean isItemTool(ItemStack par1ItemStack) {
        return true;
    }

    @Override
    public void addInformation(ItemStack stack, EntityPlayer player, List list, boolean par4) {
        AspectList cost = getVisCost();
        if (cost != null) {
            list.add(StatCollector.translateToLocal(isVisCostPerTick() ? "item.Focus.cost2" : "item.Focus.cost1"));
            addVisCostTooltip(cost, stack, player, list, par4);
        }
    }

    protected void addVisCostTooltip(AspectList cost, ItemStack stack, EntityPlayer player, List list, boolean par4) {
        for (Aspect aspect : cost.getAspectsSorted()) {
            float amount = cost.getAmount(aspect) / 100.0F;
            list.add(" " + '\u00a7' + aspect.getChatcolor() + aspect.getName() + '\u00a7' + "r x " + amount);
        }
    }

    @Override
    public int getItemEnchantability() {
        return 0;
    }

    @Override
    public EnumRarity getRarity(ItemStack itemstack) {
        return EnumRarity.rare;
    }

    @Override
    public IIcon getOrnament() {
        return ornament;
    }

    @Override
    public IIcon getFocusDepthLayerIcon() {
        return depth;
    }

    @Override
    public WandFocusAnimation getAnimation() {
        return WandFocusAnimation.WAVE;
    }

    @Override
    public boolean isVisCostPerTick() {
        return false;
    }

    @Override
    public boolean acceptsEnchant(int id) {
        return false;
    }

    @Override
    public void onPlayerStoppedUsingFocus(ItemStack itemstack, World world, EntityPlayer player, int count) {
    }

    @Override
    public boolean onFocusBlockStartBreak(ItemStack itemstack, int x, int y, int z, EntityPlayer player) {
        return false;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void registerIcons(IIconRegister iconRegister) {
        super.registerIcons(iconRegister);
        if (hasOrnament())
            ornament = iconRegister.registerIcon(Reference.MOD_IDENTIFIER + getUnlocalizedName().replace("item.", "") + "_orna");
        if (hasDepth())
            depth = iconRegister.registerIcon(Reference.MOD_IDENTIFIER + getUnlocalizedName().replace("item.", "") + "_depth");
    }
}
