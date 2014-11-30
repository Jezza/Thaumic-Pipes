package me.jezza.thaumicpipes.common.transport.messages;

import me.jezza.oc.api.network.NetworkMessageAbstract;
import me.jezza.oc.api.network.interfaces.INetworkNode;
import net.minecraft.tileentity.TileEntity;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.aspects.IAspectContainer;
import thaumcraft.common.tiles.TileThaumatorium;

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
        AspectList currentAspectList = construct.essentia;
        int amountToAdd = 0;

        aspectList:
        for (AspectList aspectList : construct.recipeEssentia)
            if (aspectList != null)
                for (Aspect aspect : aspectList.getAspects()) {
                    if (aspect != null && type.equals(aspect)) {
                        int amountNeedToAdd = aspectList.getAmount(type);
                        int currentStored = currentAspectList.getAmount(type);
                        if (amountNeedToAdd > currentStored)
                            amountToAdd = amountNeedToAdd;
                        break aspectList;
                    }
                }
        return amountToAdd;
    }
}
