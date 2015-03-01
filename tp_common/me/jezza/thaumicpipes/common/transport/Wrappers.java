package me.jezza.thaumicpipes.common.transport;

import me.jezza.oc.api.network.interfaces.INetworkNode;
import me.jezza.oc.common.utils.CoordSet;
import net.minecraft.tileentity.TileEntity;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.aspects.IAspectContainer;
import thaumcraft.api.aspects.IEssentiaTransport;

public class Wrappers {

    private Wrappers() {
    }

    public static interface IEssentiaWrapper {
        /**
         * @return how much was added
         */
        public int add(Aspect aspect, int amount);

        /**
         * @return amount that was removed.
         */
        public int remove(Aspect aspect, int amount);

        public CoordSet getCoordSet();
    }

    public static class AspectListWrapper implements IEssentiaWrapper {
        private AspectList aspectList;
        private CoordSet coordSet;

        public AspectListWrapper(AspectList aspectList, CoordSet coordSet) {
            this.aspectList = aspectList;
            this.coordSet = coordSet;
        }

        @Override
        public int add(Aspect aspect, int amount) {
            aspectList.add(aspect, amount);
            return amount;
        }

        @Override
        public int remove(Aspect aspect, int amount) {
            int startingAmount = aspectList.getAmount(aspect);
            if (startingAmount <= 0)
                return 0;
            amount = Math.min(amount, startingAmount);
            aspectList.remove(aspect, amount);
            return amount;
        }

        @Override
        public CoordSet getCoordSet() {
            return coordSet;
        }
    }

    public static class TransportWrapper implements IEssentiaWrapper {
        private IEssentiaTransport transport;
        private CoordSet coordSet;

        public TransportWrapper(IEssentiaTransport transport) {
            this.transport = transport;
            this.coordSet = new CoordSet((TileEntity) transport);
        }

        @Override
        public int add(Aspect aspect, int amount) {
            int tempAmount = amount;
            amount -= transport.addEssentia(aspect, amount, null);
            if (amount == tempAmount)
                amount = ((IAspectContainer) transport).addToContainer(aspect, amount);
            return tempAmount - amount;
        }

        @Override
        public int remove(Aspect aspect, int amount) {
            int tempAmount = amount;
            amount -= transport.takeEssentia(aspect, amount, null);
            if (amount == tempAmount)
                return ((IAspectContainer) transport).takeFromContainer(aspect, amount) ? amount : 0;
            return amount;
        }

        @Override
        public CoordSet getCoordSet() {
            return coordSet;
        }
    }

    public static class AspectWrapper {
        public final INetworkNode node;
        public final IEssentiaTransport transport;

        public boolean output = false;

        public AspectWrapper(INetworkNode node, IEssentiaTransport transport) {
            this.node = node;
            this.transport = transport;
        }

        public IEssentiaWrapper toWrapper() {
            return new TransportWrapper(transport);
        }

    }
}
