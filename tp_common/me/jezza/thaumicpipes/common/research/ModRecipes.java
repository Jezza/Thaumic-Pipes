package me.jezza.thaumicpipes.common.research;

import me.jezza.thaumicpipes.common.ModBlocks;
import me.jezza.thaumicpipes.common.lib.Strings;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import thaumcraft.api.ItemApi;
import thaumcraft.api.ThaumcraftApi;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.crafting.IArcaneRecipe;

public class ModRecipes {
    public static IArcaneRecipe thaumicPipeRecipe;

    public static void init() {
        thaumicPipeRecipe = ThaumcraftApi.addArcaneCraftingRecipe(Strings.THAUMIC_PIPE, new ItemStack(ModBlocks.thaumicPipe, 6), new AspectList().add(Aspect.ORDER, 5).add(Aspect.ENTROPY, 5), "TFT", "TET", "TFT", Character.valueOf('T'), ItemApi.getBlock("blockTube", 0), Character.valueOf('E'), new ItemStack(Items.ender_pearl), Character.valueOf('F'), ItemApi.getItem("itemResource", 8));
    }
}
