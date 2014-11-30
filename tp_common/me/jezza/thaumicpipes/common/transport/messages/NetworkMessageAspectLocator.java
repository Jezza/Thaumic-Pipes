package me.jezza.thaumicpipes.common.transport.messages;

import me.jezza.oc.api.network.NetworkResponse.MessageResponse;
import me.jezza.oc.api.network.interfaces.IMessageProcessor;
import me.jezza.oc.api.network.interfaces.INetworkNode;
import me.jezza.thaumicpipes.common.core.interfaces.IThaumicPipe;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.aspects.IAspectContainer;
import thaumcraft.common.tiles.TileJarFillable;
import thaumcraft.common.tiles.TileThaumatorium;
import thaumcraft.common.tiles.TileThaumatoriumTop;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

public class NetworkMessageAspectLocator extends NetworkMessagePipeAbstract {

    private List<IAspectContainer> constructs, jars, containers;
    private AspectList aspectList;
    private int amount;

    public NetworkMessageAspectLocator(INetworkNode owner, AspectList aspectList, Aspect filter, int amount) {
        super(owner, filter);
        constructs = new LinkedList<>();
        jars = new LinkedList<>();
        containers = new LinkedList<>();
        this.aspectList = aspectList;
        this.amount = amount;
    }

    @Override
    public void resetMessage() {
        constructs.clear();
        jars.clear();
        containers.clear();
    }

    @Override
    public MessageResponse isValidNode(INetworkNode node) {
        if (!(node instanceof IThaumicPipe))
            return MessageResponse.VALID;
        IThaumicPipe pipe = (IThaumicPipe) node;

        processConnections(constructs, pipe.getConstructConnections());
        processConnections(containers, pipe.getContainerConnections());
        processConnections(jars, pipe.getJarConnections());

        return MessageResponse.VALID;
    }

    @Override
    public MessageResponse onMessageComplete(IMessageProcessor messageProcessor) {
        if (!constructs.isEmpty()) {
            TileThaumatorium construct = null;
            for (IAspectContainer container : constructs) {
                if (container instanceof TileThaumatorium)
                    construct = (TileThaumatorium) container;
                else if (container instanceof TileThaumatoriumTop)
                    construct = ((TileThaumatoriumTop) container).thaumatorium;
                int amountToAdd = getAmountToAddToConstruct(construct, filter);
                amountToAdd = Math.min(amountToAdd, amount);
                if (amountToAdd > 0 && addTo(container, amount))
                    return MessageResponse.VALID;
            }
        }

        if (amount > 0 && !containers.isEmpty())
            for (IAspectContainer container : containers)
                if (addTo(container, amount))
                    break;

        Iterator<IAspectContainer> iterator = jars.iterator();
        if (amount > 0 && !jars.isEmpty()) {
            while (iterator.hasNext()) {
                IAspectContainer container = iterator.next();
                if (filter.equals(((TileJarFillable) container).aspectFilter)) {
                    if (addTo(container, amount))
                        break;
                    iterator.remove();
                }
            }
        }

        if (amount > 0 && !jars.isEmpty())
            for (IAspectContainer container : jars)
                if (addTo(container, 1))
                    break;

        return MessageResponse.VALID;
    }

    private boolean addTo(IAspectContainer container, int amountToAdd) {
        int leftOver = container.addToContainer(filter, amountToAdd);
        int removed = amountToAdd - leftOver;
        if (removed > 0) {
            amount -= removed;
            aspectList.remove(filter, removed);
        }
        if (amount < 0)
            amount = 0;
        return amount == 0;
    }
}
