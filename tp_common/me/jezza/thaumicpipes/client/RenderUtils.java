package me.jezza.thaumicpipes.client;

import me.jezza.thaumicpipes.common.core.TPLogger;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import thaumcraft.api.IGoggles;
import thaumcraft.api.ItemApi;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class RenderUtils {

    public static void bindTexture(ResourceLocation rl) {
        Minecraft.getMinecraft().renderEngine.bindTexture(rl);
    }

    public static boolean isPlayerWearingGoogles() {
        EntityPlayer player = Minecraft.getMinecraft().thePlayer;
        if (player == null)
            return false;
        ItemStack[] armour = player.inventory.armorInventory;

        for (ItemStack itemStack : armour) {
            if (itemStack == null)
                continue;
            if (itemStack.getItem() instanceof IGoggles)
                return true;
        }

        return false;
    }
}
