package me.jezza.thaumicpipes.common.lib;

import java.util.ArrayList;

import me.jezza.thaumicpipes.client.RenderUtils;
import me.jezza.thaumicpipes.common.core.ArmState;
import net.minecraft.util.ResourceLocation;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class TextureMaps {

    private static final String MODEL_SHEET_LOCATION = "textures/models/";

    public static int THAUMIC_TEXTURE_INDEX = 2;

    public static final ResourceLocation JAR_CONNECTION = getResource("jarConnection");

    public static final ResourceLocation[] PIPE_EXTENSION = getThaumicPipeResources("pipeExtension");
    public static final ResourceLocation[] PIPE_EXTENSION_PRIORITY = getThaumicPipeResources("pipeExtensionPriority");
    public static final ResourceLocation[] PIPE_EXTENSION_BORDERLESS = getThaumicPipeResources("pipeExtensionBorderless");
    public static final ResourceLocation[] PIPE_EXTENSION_BORDERLESS_PRIORITY = getThaumicPipeResources("pipeExtensionBorderlessPriority");
    public static final ResourceLocation[] THAUMIC_PIPE_CENTRE = getThaumicPipeResources("thaumicPipeCentre");
    public static final ResourceLocation[] THAUMIC_PIPE_ARM = getThaumicPipeResources("thaumicPipeArm");
    public static final ResourceLocation[] THAUMIC_PIPE_ARM_PRIORITY = getThaumicPipeResources("thaumicPipeArmPriority");

    public static void bindBorderlessTexture(ArmState currentState) {
        if (currentState.priority && RenderUtils.isPlayerWearingGoogles())
            RenderUtils.bindTexture(PIPE_EXTENSION_BORDERLESS_PRIORITY[THAUMIC_TEXTURE_INDEX]);
        else
            RenderUtils.bindTexture(PIPE_EXTENSION_BORDERLESS[THAUMIC_TEXTURE_INDEX]);
    }

    public static void bindBorderedTexture(ArmState currentState) {
        if (currentState.priority && RenderUtils.isPlayerWearingGoogles())
            RenderUtils.bindTexture(PIPE_EXTENSION_PRIORITY[THAUMIC_TEXTURE_INDEX]);
        else
            RenderUtils.bindTexture(PIPE_EXTENSION[THAUMIC_TEXTURE_INDEX]);
    }

    public static void bindPipeTexture(ArmState currentState) {
        if (currentState.priority && RenderUtils.isPlayerWearingGoogles())
            RenderUtils.bindTexture(THAUMIC_PIPE_ARM_PRIORITY[THAUMIC_TEXTURE_INDEX]);
        else
            RenderUtils.bindTexture(THAUMIC_PIPE_ARM[THAUMIC_TEXTURE_INDEX]);
    }

    private static ResourceLocation[] getThaumicPipeResources(String type) {
        return getResources(type + "_steamStyle", type + "_woodenStyle", type + "_goldStyle");
    }

    private static ResourceLocation[] getResources(String... loc) {
        if (loc.length <= 0)
            return null;
        ArrayList<ResourceLocation> resourceMap = new ArrayList<ResourceLocation>();
        for (int i = 0; i < loc.length; i++)
            resourceMap.add(getResource(loc[i]));
        return resourceMap.toArray(new ResourceLocation[resourceMap.size()]);
    }

    private static ResourceLocation getResource(String loc) {
        return new ResourceLocation(Reference.MOD_ID.toLowerCase(), MODEL_SHEET_LOCATION + loc + ".png");
    }

}
