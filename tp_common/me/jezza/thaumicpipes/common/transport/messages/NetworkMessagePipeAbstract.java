package me.jezza.thaumicpipes.common.transport.messages;

import me.jezza.oc.api.network.NetworkMessageAbstract;
import me.jezza.oc.api.network.interfaces.INetworkNode;
import net.minecraft.tileentity.TileEntity;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.aspects.IAspectContainer;
import thaumcraft.common.tiles.TileJarFillable;
import thaumcraft.common.tiles.TileThaumatorium;
import thaumcraft.common.tiles.TileThaumatoriumTop;

import java.util.Collection;

public abstract class NetworkMessagePipeAbstract extends NetworkMessageAbstract {

    public final Aspect filter;

    public NetworkMessagePipeAbstract(INetworkNode owner, Aspect filter) {
        super(owner);
        this.filter = filter;
    }

    public void processConnections(Collection<IAspectContainer> collection, Collection<TileEntity> connections) {
        if (connections.isEmpty())
            return;

        for (TileEntity tileEntity : connections) {
            IAspectContainer container = (IAspectContainer) tileEntity;

            if (container.doesContainerAccept(filter) && !collection.contains(container))
                collection.add(container);
        }
    }

    public int getAmountToAddToConstruct(TileThaumatorium construct, Aspect type) {
        int currentCraft = construct.currentCraft;
        if (currentCraft < 0)
            return 0;
        int amountDiff = construct.recipeEssentia.get(currentCraft).getAmount(filter) - construct.essentia.getAmount(filter);
        return amountDiff <= 0 ? 0 : amountDiff;
    }

    public TileThaumatorium getThaumatorium(IAspectContainer container) {
        return container instanceof TileThaumatorium ? (TileThaumatorium) container : container instanceof TileThaumatoriumTop ? ((TileThaumatoriumTop) container).thaumatorium : null;
    }

    /**
     * Adds to a jar, and returns the amount not added.
     */
    public int addAmountToJar(TileJarFillable jar, int amount) {
        if (jar.amount >= jar.maxAmount)
            return amount;

        Aspect aspect = jar.aspect;
        if (aspect != null && aspect != filter)
            return amount;

        Aspect aspectFilter = jar.aspectFilter;
        if (aspectFilter != null && filter != aspectFilter)
            return amount;

        return jar.addToContainer(filter, amount);
    }

    public int removeFromAddTo(IAspectContainer from, IAspectContainer to, int amount) {
        boolean flag = from.takeFromContainer(filter, amount);
        if (!flag)
            return amount;
        int amountLeft = Math.max(addTo(to, amount), 0);
        if (amountLeft > 0)
            addTo(from, amountLeft);
        return amountLeft;
    }

    public int removeFromAddTo(AspectList from, IAspectContainer to, int amount) {
        int amountInList = from.getAmount(filter);
        if (amountInList <= 0)
            return amount;

        int amountToRemove = Math.min(amountInList, amount);
        from.remove(filter, amountToRemove);
        amount -= amountToRemove;
        amount += addTo(to, amountToRemove);
        if (amount > 0)
            from.add(filter, amount);
        return amount < 0 ? 0 : amount;
    }

    public int addTo(IAspectContainer to, int amount) {
        return to instanceof TileJarFillable ? addAmountToJar((TileJarFillable) to, amount) : to.addToContainer(filter, amount);
    }

}
