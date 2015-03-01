package me.jezza.thaumicpipes.client.model;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import me.jezza.oc.client.models.ModelAbstract;
import me.jezza.thaumicpipes.common.lib.Models;

@SideOnly(Side.CLIENT)
public class ModelThaumicPipe extends ModelAbstract {

    public ModelThaumicPipe() {
        super(Models.THAUMIC_PIPE);
    }

    public void renderArm(int i) {
        renderPart("arm" + i);
    }

    public void renderVerticalParts() {
        renderOnly("arm0", "arm1");
    }
}
