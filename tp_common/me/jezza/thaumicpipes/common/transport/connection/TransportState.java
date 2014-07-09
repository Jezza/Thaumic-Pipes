package me.jezza.thaumicpipes.common.transport.connection;

import me.jezza.thaumicpipes.common.interfaces.IThaumicPipe;
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

    private ConnectionType type = ConnectionType.UNKNOWN;
    private ForgeDirection direction = ForgeDirection.UNKNOWN;

    public TransportState(TileEntity tileEntity) {
        type = ConnectionType.getConnectionType(tileEntity, true);
        switch (type) {
            case ALEMBIC:
                container = (IAspectContainer) tileEntity;
                break;
            case CONSTRUCT:
                container = (IAspectContainer) tileEntity;
                transport = (IEssentiaTransport) tileEntity;
                break;
            case JAR:
                container = (IAspectContainer) tileEntity;
                break;
            case PIPE:
                pipe = (IThaumicPipe) tileEntity;
                break;
            case REPAIRER:
                container = (IAspectContainer) tileEntity;
                break;
            default:
                break;
        }
    }

    public TransportState setDirection(ForgeDirection direction) {
        this.direction = direction;
        return this;
    }

    public ForgeDirection getDirection() {
        return direction;
    }

    public ConnectionType getType() {
        return type;
    }

    public IThaumicPipe getPipe() {
        return pipe;
    }

    public IAspectContainer getContainer() {
        return container;
    }

    public IEssentiaTransport getTransport() {
        return transport;
    }

    public int getAspectSize(Aspect filter) {
        AspectList aspectList = getAspects();
        return aspectList == null ? 0 : aspectList.getAmount(filter);
    }

    public AspectList getAspects() {
        AspectList aspectList = null;

        switch (type) {
            case ALEMBIC:
                aspectList = container.getAspects();
                break;
            case CONSTRUCT:
                aspectList = container.getAspects();
                break;
            case JAR:
                aspectList = container.getAspects();
                break;
            case PIPE:
                aspectList = pipe.getAspectList();
                break;
            case REPAIRER:
                aspectList = container.getAspects();
                break;
            default:
                break;
        }

        return aspectList;
    }

    public void removeAmount(Aspect filter, int amountToRemove) {
        switch (type) {
            case ALEMBIC:
                container.takeFromContainer(filter, amountToRemove);
                break;
            case CONSTRUCT:
                container.takeFromContainer(filter, amountToRemove);
                break;
            case JAR:
                container.takeFromContainer(filter, amountToRemove);
                break;
            case PIPE:
                pipe.removeAspect(filter, amountToRemove);
                break;
            case REPAIRER:
                container.takeFromContainer(filter, amountToRemove);
                break;
            default:
                break;
        }
    }
}
