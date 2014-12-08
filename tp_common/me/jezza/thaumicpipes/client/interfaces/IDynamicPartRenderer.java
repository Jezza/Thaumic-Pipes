package me.jezza.thaumicpipes.client.interfaces;

import me.jezza.thaumicpipes.common.multipart.pipe.PipePartAbstract;

public interface IDynamicPartRenderer {

    public void renderAt(PipePartAbstract part, double x, double y, double z, float tick);

}
