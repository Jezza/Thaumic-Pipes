package me.jezza.thaumicpipes.client.model;

import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.AdvancedModelLoader;
import net.minecraftforge.client.model.IModelCustom;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public abstract class ModelCustomAbstract {

    private IModelCustom customModel;

    public ModelCustomAbstract(ResourceLocation customModelLocation) {
        // Yeah, I'm not proud of this.
        for (int i = 0; i < 10; i++)
            try {
                this.customModel = AdvancedModelLoader.loadModel(customModelLocation);
                break;
            } catch (Exception e) {
            }
    }

    public void renderAll() {
        if (customModel != null)
            customModel.renderAll();
    }

    public void renderAllExcept(String... excludedGroupNames) {
        if (customModel != null)
            customModel.renderAllExcept(excludedGroupNames);
    }

    public void renderOnly(String... groupNames) {
        if (customModel != null)
            customModel.renderOnly(groupNames);
    }

    public void renderPart(String part) {
        if (customModel != null)
            customModel.renderPart(part);
    }
}
