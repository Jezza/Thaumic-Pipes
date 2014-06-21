package me.jezza.thaumicpipes.common.interfaces;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;

/**
 * Implement this on a tileEntity.
 */
public interface IBlockNotifier {

    public void onBlockPlaced(EntityLivingBase entityLiving, ItemStack itemStack);

    public void onBlockDestroyed();

}
