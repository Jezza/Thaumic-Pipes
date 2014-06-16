package me.jezza.thaumicpipes.client.model;

import me.jezza.thaumicpipes.common.lib.Models;
import net.minecraftforge.client.model.AdvancedModelLoader;
import net.minecraftforge.client.model.IModelCustom;

public class ModelPipeExtension {

    IModelCustom pipeExtension;

    public ModelPipeExtension() {
        pipeExtension = AdvancedModelLoader.loadModel(Models.PIPE_EXTENSION);
    }

    public void render() {
        pipeExtension.renderAll();
    }

}
