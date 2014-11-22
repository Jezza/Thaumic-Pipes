package me.jezza.thaumicpipes.common.lib;

import net.minecraft.util.ResourceLocation;

public class Models {
    public static final ResourceLocation THAUMIC_PIPE = getModelLocation("thaumicPipe");
    public static final ResourceLocation JAR_CONNECTION = getModelLocation("jarConnection");
    public static final ResourceLocation PIPE_EXTENSION = getModelLocation("pipeExtension");

    private static ResourceLocation getModelLocation(String name) {
        return new ResourceLocation(CoreProperties.MOD_ID.toLowerCase(), "models/" + name + ".obj");
    }
}
