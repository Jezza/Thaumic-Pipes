package me.jezza.thaumicpipes.client.model;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import me.jezza.thaumicpipes.common.core.TPLogger;
import me.jezza.thaumicpipes.common.lib.Models;
import net.minecraftforge.client.model.AdvancedModelLoader;
import net.minecraftforge.client.model.IModelCustom;

@SideOnly(Side.CLIENT)
public class ModelThaumicPipe {

    IModelCustom thaumicPipe;

    public ModelThaumicPipe() {
        thaumicPipe = AdvancedModelLoader.loadModel(Models.THAUMIC_PIPE);
    }

    public void renderPart(String part) {
        thaumicPipe.renderPart(part);
    }

    public void renderArm(int i) {
        if (i == 1)
            i = 2;
        else if (i == 2)
            i = 1;

        thaumicPipe.renderPart("arm" + i);
    }

    public void renderVerticalParts() {
        thaumicPipe.renderOnly("arm1", "arm2");
    }

}
