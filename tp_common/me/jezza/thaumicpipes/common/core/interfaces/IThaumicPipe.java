package me.jezza.thaumicpipes.common.core.interfaces;

import me.jezza.oc.api.network.interfaces.INetworkNode;
import me.jezza.oc.common.utils.CoordSet;
import me.jezza.thaumicpipes.common.multipart.pipe.PipePartAbstract;
import me.jezza.thaumicpipes.common.transport.connection.ArmStateHandler;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

public interface IThaumicPipe extends INetworkNode<IThaumicPipe> {

    /**
     * @return This instance.
     */
    public IThaumicPipe getPipe();

    public World world();

    public PipePartAbstract getPart();

    public CoordSet getCoordSet();

    public ArmStateHandler getArmStateHandler();

    public boolean canConnectTo(TileEntity tileEntity, ForgeDirection direction);
}
