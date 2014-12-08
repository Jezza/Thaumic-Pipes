package me.jezza.thaumicpipes.client.interfaces;

import me.jezza.thaumicpipes.common.multipart.pipe.PipePartAbstract;
import net.minecraft.client.renderer.Tessellator;

public interface IStaticPartRenderer {

    public boolean renderAt(PipePartAbstract part, Tessellator tessellator);

}
