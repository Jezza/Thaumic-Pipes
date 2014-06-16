package me.jezza.thaumicpipes.common.research;

import me.jezza.thaumicpipes.common.ModBlocks;
import me.jezza.thaumicpipes.common.core.TPLogger;
import me.jezza.thaumicpipes.common.lib.Research;
import me.jezza.thaumicpipes.common.lib.Strings;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import thaumcraft.api.ItemApi;
import thaumcraft.api.ThaumcraftApi;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.crafting.IArcaneRecipe;
import thaumcraft.api.crafting.ShapedArcaneRecipe;
import thaumcraft.api.research.ResearchCategories;
import thaumcraft.api.research.ResearchCategoryList;
import thaumcraft.api.research.ResearchItem;
import thaumcraft.api.research.ResearchPage;

public class ModResearch {
    public static void init() {
        initResearch();
    }

    private static void initResearch() {
        new ResearchItem(Strings.THAUMIC_PIPE, "ALCHEMY", new AspectList().add(Aspect.VOID, 5).add(Aspect.AIR, 5), 7, 2, 2, new ItemStack(ModBlocks.thaumicPipe)).setPages(new ResearchPage[] { new ResearchPage("tp.research_page.thaumicPipe.1"), new ResearchPage("tp.research_page.thaumicPipe.2"), new ResearchPage("tp.research_page.thaumicPipe.3"), new ResearchPage(ModRecipes.thaumicPipeRecipe) }).setParents(new String[] { "TUBEFILTER" }).setSecondary().setConcealed().registerResearchItem();
    }
}
