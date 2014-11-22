package me.jezza.thaumicpipes.client;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import me.jezza.thaumicpipes.common.lib.CoreProperties;
import me.jezza.thaumicpipes.common.lib.TextureMaps;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;
import thaumcraft.api.IGoggles;
import thaumcraft.api.ItemApi;

@SideOnly(Side.CLIENT)
public class RenderUtils {
    private static ItemStack wandStack = null;

    public static void bindTexture(ResourceLocation rl) {
        Minecraft.getMinecraft().renderEngine.bindTexture(rl);
    }

    public static void bindPriorityTexture(int animationFrame) {
        animationFrame = MathHelper.clamp_int(animationFrame, 0, CoreProperties.PIPE_ANIMATION_SIZE);
        RenderUtils.bindTexture(TextureMaps.PRIORITY_ANIMATION_FRAMES[animationFrame]);
    }

    public static void bindBorderlessTexture() {
        RenderUtils.bindTexture(TextureMaps.PIPE_EXTENSION_BORDERLESS[TextureMaps.THAUMIC_TEXTURE_INDEX]);
    }

    public static void bindBorderedTexture() {
        RenderUtils.bindTexture(TextureMaps.PIPE_EXTENSION[TextureMaps.THAUMIC_TEXTURE_INDEX]);
    }

    public static void bindPipeTexture() {
        RenderUtils.bindTexture(TextureMaps.THAUMIC_PIPE_ARM[TextureMaps.THAUMIC_TEXTURE_INDEX]);
    }

    public static boolean canRenderPriority() {
        return isPlayerHoldingWand() && isPlayerWearingGoggles();
    }

    public static boolean isPlayerWearingGoggles() {
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

    public static boolean isPlayerHoldingWand() {
        if (wandStack == null)
            wandStack = ItemApi.getItem("itemWandCasting", 0);

        EntityPlayer player = Minecraft.getMinecraft().thePlayer;
        ItemStack itemStack = player.getCurrentEquippedItem();
        return itemStack != null && wandStack != null && itemStack.getItem().equals(wandStack.getItem());
    }
}
