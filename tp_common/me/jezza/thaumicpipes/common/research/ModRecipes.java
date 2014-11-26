package me.jezza.thaumicpipes.common.research;

import me.jezza.thaumicpipes.common.ModItems;
import me.jezza.thaumicpipes.common.lib.CoreProperties;
import me.jezza.thaumicpipes.common.lib.Strings;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import thaumcraft.api.ItemApi;
import thaumcraft.api.ThaumcraftApi;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.crafting.IArcaneRecipe;
import thaumcraft.api.crafting.InfusionRecipe;
import thaumcraft.api.research.ResearchCategories;
import thaumcraft.api.research.ResearchPage;

public class ModRecipes {
    private static boolean init = false;
    private static IArcaneRecipe thaumicPipe_RecipeArcane;
    private static InfusionRecipe thaumicPipe_RecipeInfusion;

    public static ResearchPage getResearchPage() {
        return thaumicPipe_RecipeArcane != null ? new ResearchPage(thaumicPipe_RecipeArcane) : new ResearchPage(thaumicPipe_RecipeInfusion);
    }

    public static void init() {
        if (init)
            return;
        init = true;

        CoreProperties.logger.info(ResearchCategories.researchCategories);

        switch (CoreProperties.DIFFICULTY_LEVEL) {
            case 0:
                initArcaneRecipe();
                CoreProperties.logger.info("Default Arcane Recipe.");
                break;
            case 1:
                initInfusionRecipe();
                CoreProperties.logger.info("Infusion recipe.");
                break;
            case 2:
                initHardInfusionRecipe();
                CoreProperties.logger.fatal("Really hard infusion recipe.");
                break;
        }
    }

    private static void initArcaneRecipe() {
        AspectList aspectList = new AspectList();
        aspectList.add(Aspect.ORDER, 10).add(Aspect.ENTROPY, 10);
        thaumicPipe_RecipeArcane = ThaumcraftApi.addArcaneCraftingRecipe(Strings.THAUMIC_PIPE,
                new ItemStack(ModItems.thaumicPipe, 6),
                aspectList,
                "TFT",
                "CEC",
                "TFT",
                'T', ItemApi.getBlock("blockTube", 0),
                'C', ItemApi.getBlock("blockTube", 1),
                'E', new ItemStack(Items.ender_pearl),
                'F', ItemApi.getItem("itemResource", 8));
    }

    private static void initInfusionRecipe() {
        int primitiveAmount = 64;
        AspectList aspectList = new AspectList();
        aspectList.add(Aspect.AIR, primitiveAmount);
        aspectList.add(Aspect.EARTH, primitiveAmount);
        aspectList.add(Aspect.FIRE, primitiveAmount);
        aspectList.add(Aspect.WATER, primitiveAmount);
        aspectList.add(Aspect.ORDER, primitiveAmount);
        aspectList.add(Aspect.ENTROPY, primitiveAmount);

        ItemStack silverWood = ItemApi.getItem("itemResource", 8);
        ItemStack[] components = new ItemStack[6];
        int i = 0;
        components[i++] = new ItemStack(Items.ender_pearl);
        components[i++] = silverWood;
        components[i++] = new ItemStack(Items.ender_pearl);
        components[i++] = new ItemStack(Items.ender_pearl);
        components[i++] = new ItemStack(Items.ender_pearl);
        components[i] = silverWood;

        thaumicPipe_RecipeInfusion = ThaumcraftApi.addInfusionCraftingRecipe(Strings.THAUMIC_PIPE,
                new ItemStack(ModItems.thaumicPipe, 6),
                5,
                aspectList,
                ItemApi.getBlock("blockTube", 0),
                components
        );
    }

    private static void initHardInfusionRecipe() {
        int primitiveAmount = (10 * 64) + 1;
        AspectList aspectList = new AspectList();
        aspectList.add(Aspect.AIR, primitiveAmount);
        aspectList.add(Aspect.EARTH, primitiveAmount);
        aspectList.add(Aspect.FIRE, primitiveAmount);
        aspectList.add(Aspect.WATER, primitiveAmount);
        aspectList.add(Aspect.ORDER, primitiveAmount);
        aspectList.add(Aspect.ENTROPY, primitiveAmount);
        aspectList.add(Aspect.EXCHANGE, primitiveAmount);
        aspectList.add(Aspect.METAL, primitiveAmount);
        aspectList.add(Aspect.MECHANISM, primitiveAmount);
        aspectList.add(Aspect.ELDRITCH, primitiveAmount);
        aspectList.add(Aspect.MAGIC, primitiveAmount);
        aspectList.add(Aspect.ENERGY, primitiveAmount);

        ItemStack silverWood = ItemApi.getItem("itemResource", 8);
        ItemStack[] components = new ItemStack[8];
        int i = 0;
        components[i++] = new ItemStack(Items.nether_star);
        components[i++] = new ItemStack(Items.ender_pearl);
        components[i++] = silverWood;
        components[i++] = new ItemStack(Items.ender_pearl);
        components[i++] = new ItemStack(Items.nether_star);
        components[i++] = new ItemStack(Items.ender_pearl);
        components[i++] = silverWood;
        components[i] = new ItemStack(Items.ender_pearl);

        thaumicPipe_RecipeInfusion = ThaumcraftApi.addInfusionCraftingRecipe(Strings.THAUMIC_PIPE,
                new ItemStack(ModItems.thaumicPipe, 6),
                5,
                aspectList,
                ItemApi.getBlock("blockTube", 0),
                components
        );
    }
}
