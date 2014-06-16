package me.jezza.thaumicpipes.client.model;

import me.jezza.thaumicpipes.common.lib.Models;
import net.minecraftforge.client.model.AdvancedModelLoader;
import net.minecraftforge.client.model.IModelCustom;

public class ModelJarConnection {

    IModelCustom jarConnection;

    public ModelJarConnection() {
        jarConnection = AdvancedModelLoader.loadModel(Models.JAR_CONNECTION);
    }

    public void render() {
        jarConnection.renderAll();
    }

}
