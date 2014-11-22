package me.jezza.thaumicpipes.common.research;

import me.jezza.thaumicpipes.common.ModItems;
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

    private static void initResearch() {
        ResearchPage[] pages = new ResearchPage[]{
                new ResearchPage("tp.research_page.thaumicPipe.1"),
                new ResearchPage("tp.research_page.thaumicPipe.2"),
                new ResearchPage("tp.research_page.thaumicPipe.3"),
                new ResearchPage(ModRecipes.thaumicPipeRecipe)
        };
        // TODO Revisit research
        ResearchItem researchItem = new ResearchItem(Strings.THAUMIC_PIPE, "ALCHEMY", new AspectList().add(Aspect.VOID, 5).add(Aspect.AIR, 5), 7, 2, 2, new ItemStack(ModItems.thaumicPipe));
        researchItem.setPages(pages);
        researchItem.setParents("TUBEFILTER");
        researchItem.setSecondary();
        researchItem.setConcealed();
        researchItem.registerResearchItem();
    }
}
