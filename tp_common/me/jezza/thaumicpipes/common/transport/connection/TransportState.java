package me.jezza.thaumicpipes.common.transport.connection;

import me.jezza.thaumicpipes.common.interfaces.IThaumicPipe;
import net.minecraftforge.common.util.ForgeDirection;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.aspects.IAspectContainer;
import thaumcraft.api.aspects.IEssentiaTransport;

public class TransportState {

    private IThaumicPipe pipe = null;
    private IEssentiaTransport transport = null;
    private IAspectContainer container = null;

    private TransportType type = TransportType.UNKNOWN;
    private ForgeDirection direction = ForgeDirection.UNKNOWN;

    public TransportState(IThaumicPipe pipe) {
        this.pipe = pipe;
        type = TransportType.THAUMIC_PIPE;
    }

    public TransportState(IEssentiaTransport transport) {
        this.transport = transport;
        type = TransportType.CONSTRUCT;
    }

    public TransportState(IAspectContainer container) {
        this.container = container;
        type = TransportType.CONTAINER;
    }

    public TransportState setDirection(ForgeDirection direction) {
        this.direction = direction;
        return this;
    }

    public ForgeDirection getDirection() {
        return direction;
    }

    public boolean isPipe() {
        return type == TransportType.THAUMIC_PIPE;
    }

    public boolean isConstruct() {
        return type == TransportType.CONSTRUCT;
    }

    public boolean isContainer() {
        return type == TransportType.CONTAINER;
    }

    public IAspectContainer getContainer() {
        return container;
    }

    public IThaumicPipe getPipe() {
        return pipe;
    }

    public IEssentiaTransport getTransport() {
        return transport;
    }

    public int getAspectSize(Aspect filter) {
        AspectList aspectList = null;
        if (type == TransportType.THAUMIC_PIPE)
            aspectList = pipe.getAspectList();
        if (type == TransportType.CONTAINER)
            aspectList = container.getAspects();

        if (aspectList == null)
            return 0;

        return aspectList.getAmount(filter);
    }

    public void removeAmount(Aspect filter, int amountToRemove) {
        if (type == TransportType.THAUMIC_PIPE)
            pipe.removeAspect(filter, amountToRemove);
        if (type == TransportType.CONTAINER)
            container.takeFromContainer(filter, amountToRemove);
    }

    private enum TransportType {
        THAUMIC_PIPE, CONSTRUCT, CONTAINER, UNKNOWN;
    }
}
