package me.jezza.thaumicpipes.client.model;

import me.jezza.thaumicpipes.common.lib.Models;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ModelThaumicPipe extends ModelCustomAbstract {

    public ModelThaumicPipe() {
        super(Models.THAUMIC_PIPE);
    }

    public void renderArm(int i) {
        if (i == 1)
            i = 2;
        else if (i == 2)
            i = 1;

        renderPart("arm" + i);
    }

    public void renderVerticalParts() {
        renderOnly("arm1", "arm2");
    }

}
