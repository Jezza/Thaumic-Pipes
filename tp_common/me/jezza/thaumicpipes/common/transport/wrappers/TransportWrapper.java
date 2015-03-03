package me.jezza.thaumicpipes.common.transport.wrappers;

import me.jezza.oc.api.network.interfaces.INetworkNode;
import me.jezza.thaumicpipes.common.core.interfaces.IEssentiaWrapper;
import net.minecraftforge.common.util.ForgeDirection;
import thaumcraft.api.aspects.IEssentiaTransport;

public class TransportWrapper {
    public final INetworkNode node;
    public final IEssentiaTransport transport;
    public final ForgeDirection direction;

    public boolean output = false;

    public TransportWrapper(INetworkNode node, IEssentiaTransport transport, ForgeDirection direction) {
        this.node = node;
        this.transport = transport;
        this.direction = direction;
    }

    public IEssentiaWrapper toWrapper() {
        return new EssentiaWrapper(transport, direction);
    }
}
