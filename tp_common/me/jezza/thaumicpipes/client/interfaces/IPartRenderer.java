package me.jezza.thaumicpipes.client.interfaces;

import me.jezza.thaumicpipes.common.multipart.pipe.PipePartAbstract;

public interface IPartRenderer {

    public void renderAt(PipePartAbstract part, double x, double y, double z, float tick);

}
