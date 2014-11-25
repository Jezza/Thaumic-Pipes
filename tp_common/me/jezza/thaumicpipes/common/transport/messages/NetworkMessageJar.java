package me.jezza.thaumicpipes.common.transport.messages;

import me.jezza.oc.api.NetworkResponse.MessageResponse;
import me.jezza.oc.api.interfaces.IMessageProcessor;
import me.jezza.oc.api.interfaces.INetworkNode;
import me.jezza.thaumicpipes.common.core.interfaces.IThaumicPipe;
import net.minecraft.tileentity.TileEntity;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.IAspectContainer;
import thaumcraft.common.tiles.TileJarFillable;
import thaumcraft.common.tiles.TileThaumatorium;
import thaumcraft.common.tiles.TileThaumatoriumTop;

import java.util.Collection;
import java.util.LinkedList;

public class NetworkMessageJar extends NetworkMessagePipeAbstract {
    private Collection<IAspectContainer> constructs, containers, jars;
    private IAspectContainer mainJar;
    private int amount;
    private boolean fromFiltered;

    public NetworkMessageJar(INetworkNode owner, IAspectContainer mainJar, Aspect filter, int amount, boolean fromFiltered) {
        super(owner, filter);
        constructs = new LinkedList<>();
        containers = new LinkedList<>();
        jars = new LinkedList<>();
        this.mainJar = mainJar;
        this.amount = amount;
        this.fromFiltered = fromFiltered;
    }

    @Override
    public void resetMessage() {
        constructs.clear();
        containers.clear();
        jars.clear();
    }

    @Override
    public MessageResponse isValidNode(INetworkNode node) {
        if (!(node instanceof IThaumicPipe))
            return MessageResponse.VALID;
        IThaumicPipe pipe = (IThaumicPipe) node;

        processConnections(constructs, pipe.getConstructConnections());
        processConnections(containers, pipe.getContainerConnections());
        if (!fromFiltered)
            processJars(jars, pipe.getJarConnections());

        return MessageResponse.VALID;
    }

    private void processJars(Collection<IAspectContainer> collection, Collection<TileEntity> connections) {
        if (connections.isEmpty())
            return;

        for (TileEntity tileEntity : connections) {
            TileJarFillable jar = (TileJarFillable) tileEntity;

            Aspect aspect = jar.aspectFilter;
            if (aspect != null && filter.equals(aspect) && !collection.contains(jar))
                collection.add(jar);
        }
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
                if (amountToAdd > 0 && addTo(construct, amount))
                    break;
            }
        }

        if (amount > 0 && !containers.isEmpty())
            for (IAspectContainer container : containers)
                if (addTo(container, amount))
                    break;

        if (amount > 0 && !jars.isEmpty())
            for (IAspectContainer jar : jars)
                if (addTo(jar, amount))
                    break;

        return MessageResponse.VALID;
    }

    private boolean addTo(IAspectContainer container, int amountToAdd) {
        boolean flag = mainJar.takeFromContainer(filter, amountToAdd);
        if (!flag)
            return amount == 0;
        int leftOver = container.addToContainer(filter, amountToAdd);
        amount -= (amountToAdd - leftOver);
        if (amount < 0)
            amount = 0;
        return amount == 0;
    }
}
