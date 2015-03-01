package me.jezza.thaumicpipes.common.research;

import cpw.mods.fml.common.Loader;
import me.jezza.oc.common.utils.Localise;
import me.jezza.thaumicpipes.common.ModItems;
import me.jezza.thaumicpipes.common.lib.Strings;
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
            count = Integer.parseInt(Localise.format("tp.research_page.thaumicPipe.count"));
        } catch (Exception e) {
            count = 3;
        }
        ResearchPage[] pages = new ResearchPage[count + 1];
        StringBuilder searchString = new StringBuilder("tp.research_page.thaumicPipe.");
        for (int i = 1; i <= count; i++)
            pages[i - 1] = new ResearchPage(searchString.toString() + i);
        pages[count] = ModRecipes.getResearchPage();

        String category = "TP_CATEGORY";
        String parent = "";
        int col = 1;
        int row = 1;
        if (Loader.isModLoaded("ThaumicTinkerer")) {
            category = "TT_CATEGORY";
            parent = "FUNNEL";
            col = 1;
            row = -9;
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
