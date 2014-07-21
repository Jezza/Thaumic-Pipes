package me.jezza.thaumicpipes.api.interfaces;

import me.jezza.thaumicpipes.common.multipart.pipe.PipePartAbstract;
import net.minecraftforge.common.util.ForgeDirection;

public interface IThaumicPipe {

    public boolean canConnectTo(ForgeDirection direction);

    public void drain();

    public PipePartAbstract getPipe();
}
