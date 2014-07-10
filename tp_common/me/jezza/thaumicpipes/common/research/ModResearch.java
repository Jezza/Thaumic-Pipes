package me.jezza.thaumicpipes.common.research;

import me.jezza.thaumicpipes.common.ModBlocks;
import me.jezza.thaumicpipes.common.lib.Strings;
import net.minecraft.item.ItemStack;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.research.ResearchItem;
import thaumcraft.api.research.ResearchPage;

public class ModResearch {
    public static void init() {
        initResearch();
    }

    // @formatter:off
    private static void initResearch() {
        ResearchPage[] pages = new ResearchPage[] {
                new ResearchPage("tp.research_page.thaumicPipe.1"),
                new ResearchPage("tp.research_page.thaumicPipe.2"),
                new ResearchPage("tp.research_page.thaumicPipe.3"),
                new ResearchPage(ModRecipes.thaumicPipeRecipe)
        };
        // TODO Revisit research
        new ResearchItem(Strings.THAUMIC_PIPE, "ALCHEMY", new AspectList().add(Aspect.VOID, 5).add(Aspect.AIR, 5), 7, 2, 2, new ItemStack(ModBlocks.thaumicPipe)).setPages(pages).setParents(new String[] { "TUBEFILTER" }).setSecondary().setConcealed().registerResearchItem();
    }
    // @formatter:on
}
