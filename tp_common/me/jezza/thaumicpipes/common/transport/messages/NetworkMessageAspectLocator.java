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
            for (IAspectContainer container : constructs) {
                TileThaumatorium construct = getThaumatorium(container);
                if (construct == null)
                    continue;
                int amountToAdd = getAmountToAddToConstruct(construct, filter);
                amountToAdd = Math.min(amountToAdd, amount);
                if (amountToAdd > 0) {
                    amount -= amountToAdd;
                    amountToAdd = removeFromAddTo(aspectList, construct, amountToAdd);
                    amount += amountToAdd;
                    if (amount == 0)
                        return MessageResponse.VALID;
                }
            }
        }

        if (amount > 0 && !containers.isEmpty())
            for (IAspectContainer container : containers) {
                amount = removeFromAddTo(aspectList, container, amount);
                if (amount == 0)
                    return MessageResponse.VALID;
            }

        if (amount > 0 && !jars.isEmpty()) {
            Iterator<IAspectContainer> iterator = jars.iterator();
            while (iterator.hasNext()) {
                IAspectContainer container = iterator.next();
                if (filter == ((TileJarFillable) container).aspectFilter) {
                    amount = removeFromAddTo(aspectList, container, amount);
                    if (amount == 0)
                        return MessageResponse.VALID;
                    iterator.remove();
                }
            }
        }

        if (amount > 0 && !jars.isEmpty()) {
            Iterator<IAspectContainer> iterator = jars.iterator();
            while (iterator.hasNext()) {
                TileJarFillable jar = (TileJarFillable) iterator.next();
                if (jar.amount > 0 && filter.equals(jar.aspect)) {
                    amount = removeFromAddTo(aspectList, jar, amount);
                    if (amount == 0)
                        return MessageResponse.VALID;
                    iterator.remove();
                }
            }
        }

        if (amount > 0 && !jars.isEmpty())
            for (IAspectContainer container : jars) {
                amount = removeFromAddTo(aspectList, container, amount);
                if (amount == 0)
                    return MessageResponse.VALID;
            }

        return MessageResponse.VALID;
    }
}
