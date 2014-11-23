package me.jezza.thaumicpipes.common.transport.messages;

import me.jezza.oc.api.NetworkResponse.MessageResponse;
import me.jezza.oc.api.abstracts.NetworkMessageAbstract;
import me.jezza.oc.api.interfaces.IMessageProcessor;
import me.jezza.oc.api.interfaces.INetworkNode;
import me.jezza.thaumicpipes.common.core.interfaces.IThaumicPipe;
import net.minecraft.tileentity.TileEntity;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.aspects.IAspectContainer;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

public class NetworkMessageAspectLocator extends NetworkMessageAbstract {

    private List<IAspectContainer> containers;
    private Aspect filter;
    private int amount;

    public NetworkMessageAspectLocator(INetworkNode owner, Aspect filter, int amount) {
        super(owner);
        containers = new LinkedList<>();
        this.filter = filter;
        this.amount = amount;
    }

    @Override
    public void resetMessage() {
        containers.clear();
    }

    @Override
    public MessageResponse isValidNode(INetworkNode node) {
        if (!(node instanceof IThaumicPipe))
            return MessageResponse.VALID;
        IThaumicPipe pipe = (IThaumicPipe) node;

        processConnections(pipe.getConstructConnections());
        processConnections(pipe.getContainerConnections());
        processConnections(pipe.getJarConnections());

        return MessageResponse.VALID;
    }

    private void processConnections(Collection<TileEntity> containers) {
        if (containers.isEmpty())
            return;

        for (TileEntity tileEntity : containers) {
            IAspectContainer container = (IAspectContainer) tileEntity;

            if (filter == null) {
                if (!this.containers.contains(container))
                    this.containers.add(container);
                continue;
            }
            if (container.doesContainerAccept(filter) && !this.containers.contains(container))
                this.containers.add(container);
        }

    }

    @Override
    public MessageResponse onMessageComplete(IMessageProcessor messageProcessor) {
        IThaumicPipe pipe = (IThaumicPipe) getOwner();
        AspectList aspectList = pipe.getPendingAspects();
        int currentAmount = aspectList.getAmount(filter);

        for (IAspectContainer container : containers) {
            int result = amount - container.addToContainer(filter, amount);
            if (result > 0) {
                aspectList.remove(filter, result);
                currentAmount -= result;
                if (currentAmount <= 0)
                    break;
            }
        }

        return MessageResponse.VALID;
    }
}
