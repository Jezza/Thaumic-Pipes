package me.jezza.thaumicpipes.client.lib;

import me.jezza.oc.api.configuration.Config.ConfigInteger;
import me.jezza.thaumicpipes.common.lib.CoreProperties;
import net.minecraft.client.Minecraft;
import net.minecraft.util.ResourceLocation;

import java.util.ArrayList;
import java.util.Collection;

public class TextureMaps {

    private static final String MODEL_SHEET_LOCATION = "textures/models/";

    @ConfigInteger(category = "Gameplay", minValue = 0, maxValue = 2, comment = {"0 - Steam punk style texture.", "1 - Old thaumcraft style texture.", "2 - The default texture."})
    public static int THAUMIC_TEXTURE_INDEX = 2;

    public static final ResourceLocation JAR_CONNECTION = getResource("jarConnection");
    public static final ResourceLocation[] THAUMIC_PIPE = getPipeResources("thaumicPipe");

    private static ResourceLocation[] getPipeResources(String type) {
        ArrayList<String> textures = new ArrayList<>();
        for (Themes theme : Themes.values())
            textures.add(type + theme);
        return getResources(textures);
    }

    private static ResourceLocation[] getResources(Collection<String> locations) {
        if (locations.isEmpty())
            return null;

        ResourceLocation[] resourceArray = new ResourceLocation[locations.size()];
        int index = 0;
        for (String s : locations)
            resourceArray[index++] = getResource(s);
        return resourceArray;
    }

    private static ResourceLocation getResource(String loc) {
        return new ResourceLocation(CoreProperties.MOD_ID.toLowerCase(), MODEL_SHEET_LOCATION + loc + ".png");
    }

    public static void bindTexture(ResourceLocation rl) {
        Minecraft.getMinecraft().renderEngine.bindTexture(rl);
    }

    public static void bindPipeTexture() {
        bindTexture(THAUMIC_PIPE[THAUMIC_TEXTURE_INDEX]);
    }

    private static enum Themes {
        STEAM, WOODEN, GOLD;

        @Override
        public String toString() {
            return "_" + name().toLowerCase();
        }
    }
}
