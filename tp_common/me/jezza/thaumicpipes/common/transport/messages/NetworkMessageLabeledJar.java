package me.jezza.thaumicpipes.common.transport.messages;

import me.jezza.oc.api.NetworkResponse.MessageResponse;
import me.jezza.oc.api.abstracts.NetworkMessageAbstract;
import me.jezza.oc.api.interfaces.IMessageProcessor;
import me.jezza.oc.api.interfaces.INetworkNode;
import me.jezza.thaumicpipes.common.core.interfaces.IThaumicPipe;
import net.minecraft.tileentity.TileEntity;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.IAspectContainer;
import thaumcraft.common.tiles.TileJarFillable;

import java.util.Collection;
import java.util.LinkedList;

public class NetworkMessageLabeledJar extends NetworkMessageAbstract {

    private Collection<IAspectContainer> jars;
    private IAspectContainer mainJar;
    private Aspect filter;


    public NetworkMessageLabeledJar(INetworkNode owner, Aspect filter, IAspectContainer mainJar) {
        super(owner);
        jars = new LinkedList<>();
        this.filter = filter;
        this.mainJar = mainJar;
    }

    @Override
    public void resetMessage() {

    }

    @Override
    public MessageResponse isValidNode(INetworkNode node) {
        if (!(node instanceof IThaumicPipe))
            return MessageResponse.VALID;
        IThaumicPipe pipe = (IThaumicPipe) node;

        processConnections(jars, pipe.getJarConnections());

        return MessageResponse.VALID;
    }

    private void processConnections(Collection<IAspectContainer> collection, Collection<TileEntity> connections) {
        if (connections.isEmpty())
            return;

        for (TileEntity tileEntity : connections) {
            TileJarFillable jar = (TileJarFillable) tileEntity;

            if (jar.aspectFilter != null)
                continue;

            if (jar.containerContains(filter) > 0 && !collection.contains(jar))
                collection.add(jar);
        }
    }

    @Override
    public MessageResponse onMessageComplete(IMessageProcessor messageProcessor) {
        if (!jars.isEmpty())
            for (IAspectContainer container : jars)
                if (removeFrom(container))
                    break;

        return MessageResponse.VALID;
    }

    private boolean removeFrom(IAspectContainer jar) {
        boolean removed = jar.takeFromContainer(filter, 1);
        if (removed) {
            int leftOver = mainJar.addToContainer(filter, 1);
            if (leftOver == 1)
                return true;
        }
        return false;
    }

}
