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
import thaumcraft.common.tiles.TileJarFillable;
import thaumcraft.common.tiles.TileThaumatorium;
import thaumcraft.common.tiles.TileThaumatoriumTop;

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

public class NetworkMessageAspectLocator extends NetworkMessageAbstract {

    private List<IAspectContainer> constructs;
    private List<IAspectContainer> jars;
    private List<IAspectContainer> containers;
    private AspectList aspectList;
    private Aspect filter;
    private int amount;

    public NetworkMessageAspectLocator(INetworkNode owner, AspectList aspectList, Aspect filter, int amount) {
        super(owner);
        constructs = new LinkedList<>();
        jars = new LinkedList<>();
        containers = new LinkedList<>();
        this.aspectList = aspectList;
        this.filter = filter;
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

    private void processConnections(Collection<IAspectContainer> collection, Collection<TileEntity> connections) {
        if (connections.isEmpty())
            return;

        for (TileEntity tileEntity : connections) {
            IAspectContainer container = (IAspectContainer) tileEntity;

            if (container.doesContainerAccept(filter) && !collection.contains(container))
                collection.add(container);
        }
    }

    @Override
    public MessageResponse onMessageComplete(IMessageProcessor messageProcessor) {
        TileThaumatorium construct = null;
        if (!constructs.isEmpty())
            for (IAspectContainer container : constructs) {
                if (container instanceof TileThaumatorium)
                    construct = (TileThaumatorium) container;
                else if (container instanceof TileThaumatoriumTop)
                    construct = ((TileThaumatoriumTop) container).thaumatorium;

                int amountToAdd = -1;

                AspectList currentAspectList = construct.essentia;

                aspectList:
                for (AspectList aspectList : construct.recipeEssentia)
                    if (aspectList != null)
                        for (Aspect aspect : aspectList.getAspects()) {
                            if (aspect != null && filter.equals(aspect)) {
                                int amountNeedToAdd = aspectList.getAmount(filter);
                                int currentStored = currentAspectList.getAmount(filter);
                                if (amountNeedToAdd > currentStored)
                                    amountToAdd = amountNeedToAdd;
                                break aspectList;
                            }
                        }

                if (amountToAdd > 0 && addToConstructs(container, 1))
                    break;
            }

        if (amount > 0 && !containers.isEmpty())
            for (IAspectContainer container : containers)
                if (addTo(container))
                    break;

        Iterator<IAspectContainer> iterator = jars.iterator();
        if (amount > 0 && !jars.isEmpty()) {
            while (iterator.hasNext()) {
                IAspectContainer container = iterator.next();
                if (filter.equals(((TileJarFillable) container).aspectFilter)) {
                    if (addTo(container))
                        break;
                    iterator.remove();
                }
            }
        }

        if (amount > 0 && !jars.isEmpty())
            for (IAspectContainer container : jars) {
                if (addTo(container))
                    break;
            }

        return MessageResponse.VALID;
    }

    private boolean addTo(IAspectContainer container) {
        boolean containerFull = false;
        while (amount > 0 && !containerFull) {
            int leftOver = container.addToContainer(filter, amount);
            int removed = amount - leftOver;
            amount -= removed;
            aspectList.remove(filter, removed);
            if (amount < 0)
                amount = 0;
            containerFull = leftOver == amount;
        }
        return amount == 0;
    }

    private boolean addToConstructs(IAspectContainer construct, int amountToAdd) {
        int leftOver = construct.addToContainer(filter, amountToAdd);
        int removed = amountToAdd - leftOver;
        amount -= removed;
        aspectList.remove(filter, removed);
        if (amount < 0)
            amount = 0;
        return amount == 0;
    }


}
