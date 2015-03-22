package me.jezza.thaumicpipes.common.transport.wrappers;

import me.jezza.oc.common.utils.CoordSet;
import me.jezza.thaumicpipes.common.core.interfaces.IEssentiaWrapper;
import me.jezza.thaumicpipes.common.core.interfaces.IThaumicPipe;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.util.ForgeDirection;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.IAspectContainer;
import thaumcraft.api.aspects.IEssentiaTransport;

public class EssentiaTransportWrapper implements Comparable<EssentiaTransportWrapper>, IEssentiaWrapper {

    // TODO Fix up armStateHandler.

    public final IThaumicPipe pipe;
    public final CoordSet coordSet;
    public final ForgeDirection direction;
    public final IEssentiaTransport transport;
    public final boolean output;

    private IAspectContainer container;

    public EssentiaTransportWrapper(IThaumicPipe pipe, TileEntity transport, ForgeDirection direction) {
        this(pipe, transport, direction, false);
    }

    public EssentiaTransportWrapper(IThaumicPipe pipe, TileEntity transport, ForgeDirection direction, boolean isOutput) {
        this.pipe = pipe;
        this.transport = (IEssentiaTransport) transport;
        if (transport instanceof IAspectContainer)
            container = (IAspectContainer) transport;
        this.direction = direction.getOpposite();
        this.output = isOutput;
        coordSet = new CoordSet(transport);
    }

    public boolean isConnectable() {
        return transport.isConnectable(direction);
    }

    public boolean canInputFrom() {
        return transport.canInputFrom(direction);
    }

    public boolean canOutputTo() {
        return transport.canOutputTo(direction);
    }

    public void setSuction(Aspect aspect, int amount) {
        transport.setSuction(aspect, amount);
    }

    public Aspect getSuctionType() {
        return transport.getSuctionType(direction);
    }

    public int getSuctionAmount() {
        return transport.getSuctionAmount(direction);
    }

    public int takeEssentia(Aspect aspect, int amount) {
        return transport.takeEssentia(aspect, amount, direction);
    }

    @Override
    public int remove(Aspect aspect, int amount) {
        int tempAmount = amount - takeEssentia(aspect, amount);
        if (amount == tempAmount && container != null)
            return container.takeFromContainer(aspect, amount) ? amount : 0;
        return tempAmount;
    }

    public int addEssentia(Aspect aspect, int amount) {
        return transport.addEssentia(aspect, amount, direction);
    }

    @Override
    public int add(Aspect aspect, int amount) {
        int tempAmount = amount - addEssentia(aspect, amount);
        if (amount == tempAmount && container != null)
            tempAmount = container.addToContainer(aspect, amount);
        return amount - tempAmount;
    }

    public Aspect getEssentiaType() {
        return transport.getEssentiaType(direction);
    }

    public int getEssentiaAmount() {
        return transport.getEssentiaAmount(direction);
    }

    public int getMinimumSuction() {
        return transport.getMinimumSuction();
    }

    public boolean renderExtendedTube() {
        return transport.renderExtendedTube();
    }

    public boolean shouldSkip() {
        return getMinimumSuction() > getSuctionAmount();
    }

    @Override
    public CoordSet getCoordSet() {
        return coordSet;
    }

    /**
     * This checks the objects against one another.
     *
     * @param other is the object already in the collection.
     * @return 1 if the object compared with is greater than this one,
     * -1 if the object is less than this one,
     * and 0 if the objects are the same.
     */
    @Override
    public int compareTo(EssentiaTransportWrapper other) {
        if (other.output && output) {
            int compare = Integer.compare(other.getSuctionAmount(), getSuctionAmount());
            return compare != 0 ? compare : 1;
        }
        if (other.output)
            return 1;
        if (output)
            return -1;
        int compare = Integer.compare(other.getSuctionAmount(), getSuctionAmount());
        if (compare != 0)
            return compare;
        compare = Integer.compare(getEssentiaAmount(), other.getEssentiaAmount());
        return compare != 0 ? compare : 1;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        return coordSet.equals(((EssentiaTransportWrapper) o).coordSet);
    }

    @Override
    public int hashCode() {
        return coordSet.hashCode();
    }
}
