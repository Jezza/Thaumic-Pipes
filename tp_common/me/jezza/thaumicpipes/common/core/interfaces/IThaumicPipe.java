package me.jezza.thaumicpipes.common.core.interfaces;

import com.sun.istack.internal.NotNull;
import me.jezza.thaumicpipes.common.multipart.pipe.PipePartAbstract;
import me.jezza.thaumicpipes.common.transport.connection.ArmStateHandler;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.util.ForgeDirection;

public interface IThaumicPipe {

    @NotNull
    public PipePartAbstract getPipe();

    @NotNull
    public ArmStateHandler getArmStateHandler();

    public boolean canConnectTo(TileEntity tileEntity, ForgeDirection direction);
}
