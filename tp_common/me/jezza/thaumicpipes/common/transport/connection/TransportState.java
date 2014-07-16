package me.jezza.thaumicpipes.common.transport.connection;

import me.jezza.thaumicpipes.api.interfaces.IThaumicPipe;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.util.ForgeDirection;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.aspects.IAspectContainer;
import thaumcraft.api.aspects.IEssentiaTransport;

public class TransportState {

    private IThaumicPipe pipe = null;
    private IAspectContainer container = null;
    private IEssentiaTransport transport = null;

    private ForgeDirection direction = ForgeDirection.UNKNOWN;

    public TransportState(TileEntity tileEntity, ForgeDirection direction) {
        this.direction = direction;

        if (tileEntity instanceof IAspectContainer)
            container = (IAspectContainer) tileEntity;
        if (tileEntity instanceof IEssentiaTransport)
            transport = (IEssentiaTransport) tileEntity;
        if (tileEntity instanceof IThaumicPipe)
            pipe = (IThaumicPipe) tileEntity;
    }

    public ForgeDirection getDirection() {
        return direction;
    }

    public boolean isPipe() {
        return pipe != null;
    }

    public IThaumicPipe getPipe() {
        return pipe;
    }

    public boolean isContainer() {
        return container != null;
    }

    public IAspectContainer getContainer() {
        return container;
    }

    public boolean isTransport() {
        return container != null;
    }

    public IEssentiaTransport getTransport() {
        return transport;
    }

    public int getAspectSize(Aspect aspect) {
        AspectList aspectList = getAspects();
        return aspectList == null ? 0 : aspectList.getAmount(aspect);
    }

    public AspectList getAspects() {
        AspectList aspectList = null;

        if (isContainer())
            aspectList = container.getAspects();
        else if (isPipe())
            aspectList = pipe.getAspectList();

        return aspectList;
    }

    public void removeAmount(Aspect aspect, int amountToRemove) {
        if (isContainer())
            container.takeFromContainer(aspect, amountToRemove);
        else if (isPipe())
            pipe.removeAspect(aspect, amountToRemove);
    }
}
