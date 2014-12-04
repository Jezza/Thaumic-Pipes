package me.jezza.thaumicpipes.common.transport.messages;

import me.jezza.oc.api.network.NetworkResponse.MessageResponse;
import me.jezza.oc.api.network.interfaces.IMessageProcessor;
import me.jezza.oc.api.network.interfaces.INetworkNode;
import me.jezza.thaumicpipes.common.core.interfaces.IThaumicPipe;
import net.minecraft.tileentity.TileEntity;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.IAspectContainer;
import thaumcraft.common.tiles.TileJarFillable;
import thaumcraft.common.tiles.TileThaumatorium;

import java.util.Collection;
import java.util.LinkedList;

public class NetworkMessageJar extends NetworkMessagePipeAbstract {
    private Collection<IAspectContainer> constructs, containers;
    private Collection<TileJarFillable> jars;
    private TileJarFillable mainJar;
    private int amount;
    private boolean fromFiltered;

    public NetworkMessageJar(INetworkNode owner, IAspectContainer mainJar, Aspect filter, int amount, boolean fromFiltered) {
        super(owner, filter);
        constructs = new LinkedList<>();
        containers = new LinkedList<>();
        jars = new LinkedList<>();
        this.mainJar = (TileJarFillable) mainJar;
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
        processJars(jars, pipe.getJarConnections());

        return MessageResponse.VALID;
    }

    private void processJars(Collection<TileJarFillable> collection, Collection<TileEntity> connections) {
        if (connections.isEmpty())
            return;

        for (TileEntity tileEntity : connections) {
            TileJarFillable jar = (TileJarFillable) tileEntity;

            if (this.mainJar.equals(jar))
                continue;

            if (jar.aspect == null || jar.amount == jar.maxAmount)
                continue;

            Aspect aspect = jar.aspectFilter;
            if (aspect != null && filter == aspect && !collection.contains(jar))
                collection.add(jar);
        }
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
                    amountToAdd = removeFromAddTo(mainJar, construct, amountToAdd);
                    amount += amountToAdd;
                    if (amount == 0)
                        return MessageResponse.VALID;
                }
            }
        }

        if (amount > 0 && !containers.isEmpty())
            for (IAspectContainer container : containers) {
                amount = removeFromAddTo(mainJar, container, amount);
                if (amount == 0)
                    return MessageResponse.VALID;
            }

        if (amount > 0 && !jars.isEmpty())
            if (fromFiltered) {
                for (TileJarFillable jar : jars)
                    if (mainJar.amount > jar.amount + 1) {
                        amount = removeFromAddTo(mainJar, jar, amount);
                        if (amount == 0)
                            break;
                    }
            } else {
                for (IAspectContainer jar : jars) {
                    amount = removeFromAddTo(mainJar, jar, amount);
                    if (amount == 0)
                        break;
                }
            }

        return MessageResponse.VALID;
    }
}
