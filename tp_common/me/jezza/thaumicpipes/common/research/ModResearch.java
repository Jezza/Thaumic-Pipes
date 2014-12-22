package me.jezza.thaumicpipes.common.research;

import cpw.mods.fml.common.Loader;
import me.jezza.thaumicpipes.common.ModItems;
import me.jezza.thaumicpipes.common.lib.Strings;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.research.ResearchCategories;
import thaumcraft.api.research.ResearchItem;
import thaumcraft.api.research.ResearchPage;

public class ModResearch {
    private static boolean init = false;

    public static void init() {
        if (init)
            return;
        init = true;
        initResearch();
    }

    private static void initResearch() {

        int count;
        try {
            count = Integer.valueOf(I18n.format("tp.research_page.thaumicPipe.count"));
        } catch (NumberFormatException e) {
            count = 3;
        }

        ResearchPage[] pages = new ResearchPage[count + 1];
        for (int i = 1; i <= count; i++)
            pages[i - 1] = new ResearchPage("tp.research_page.thaumicPipe." + i);
        pages[count] = ModRecipes.getResearchPage();

        String category = "TP_CATEGORY";
        String parent = "";
        int col = 1;
        int row = 1;
        if (Loader.isModLoaded("ThaumicTinkerer")) {
            category = "TT_CATEGORY";
            parent = "REPAIRER";
            col = -2;
            row = -11;
        } else
            initResearchCategory();


        ResearchItem researchItem = new ResearchItem(Strings.THAUMIC_PIPE, category, new AspectList().add(Aspect.VOID, 10).add(Aspect.AIR, 10), col, row, 2, new ItemStack(ModItems.thaumicPipe));
        researchItem.setPages(pages);
        if (!parent.equals(""))
            researchItem.setParents(parent);
        researchItem.setSecondary();
        researchItem.setConcealed();
        researchItem.registerResearchItem();
    }

    private static void initResearchCategory() {
        ResourceLocation background = new ResourceLocation("thaumcraft", "textures/gui/gui_researchback.png");
        ResearchCategories.registerCategory("TP_CATEGORY", new ResourceLocation("thaumicpipes:textures/misc/r_thaumicPipes.png"), background);
    }

}
