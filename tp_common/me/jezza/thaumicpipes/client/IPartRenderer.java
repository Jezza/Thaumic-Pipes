package me.jezza.thaumicpipes.client;

import me.jezza.thaumicpipes.common.multipart.part.PipePartAbstract;

public interface IPartRenderer {

    public void renderAt(PipePartAbstract part, double x, double y, double z, float frame);

}
