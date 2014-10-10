package me.jezza.thaumicpipes.common.transport.connection;

import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.util.ForgeDirection;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.aspects.IAspectContainer;
import thaumcraft.api.aspects.IEssentiaTransport;

public class TransportState {

    private IAspectContainer container = null;
    private IEssentiaTransport transport = null;

    private ForgeDirection direction = ForgeDirection.UNKNOWN;

    public TransportState(TileEntity tileEntity, ForgeDirection direction) {
        this.direction = direction;

        if (tileEntity instanceof IAspectContainer)
            container = (IAspectContainer) tileEntity;
        if (tileEntity instanceof IEssentiaTransport)
            transport = (IEssentiaTransport) tileEntity;
    }

    public ForgeDirection getDirection() {
        return direction;
    }

    public boolean isContainer() {
        return container != null;
    }

    public IAspectContainer getContainer() {
        return container;
    }

    public boolean isTransport() {
        return transport != null;
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

        return aspectList;
    }

    public boolean removeAmount(Aspect aspect, int amountToRemove) {
        if (isContainer())
            return container.takeFromContainer(aspect, amountToRemove);
        return false;
    }
}
