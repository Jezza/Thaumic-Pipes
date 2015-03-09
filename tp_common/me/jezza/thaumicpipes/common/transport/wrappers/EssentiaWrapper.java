package me.jezza.thaumicpipes.common.transport.wrappers;

import me.jezza.oc.common.utils.CoordSet;
import me.jezza.thaumicpipes.common.core.interfaces.IEssentiaWrapper;
import me.jezza.thaumicpipes.common.lib.CoreProperties;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.util.ForgeDirection;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.IAspectContainer;
import thaumcraft.api.aspects.IEssentiaTransport;

public class EssentiaWrapper implements IEssentiaWrapper {

    private static boolean fired = false;

    private IEssentiaTransport transport;
    private CoordSet coordSet;
    private ForgeDirection direction;

    public EssentiaWrapper(IEssentiaTransport transport, ForgeDirection direction) {
        this.transport = transport;
        this.coordSet = new CoordSet((TileEntity) transport);
        this.direction = direction.getOpposite();
    }

    @Override
    public int add(Aspect aspect, int amount) {
        int tempAmount = amount;
//        try {
            amount -= transport.addEssentia(aspect, amount, direction);
            if (amount == tempAmount)
                amount = ((IAspectContainer) transport).addToContainer(aspect, amount);
//        } catch (RuntimeException ignored) {
//            if (!fired) {
//                fired = true;
                CoreProperties.logger.fatal("Please go yell at Azanor. Courtesy of Jezza");
                CoreProperties.logger.fatal("Please go yell at Azanor. Courtesy of Jezza");
                CoreProperties.logger.fatal("Please go yell at Azanor. Courtesy of Jezza");
                CoreProperties.logger.fatal("Please go yell at Azanor. Courtesy of Jezza");
                CoreProperties.logger.fatal("Please go yell at Azanor. Courtesy of Jezza");
//            }
//            return tempAmount - amount;
//        }
        return tempAmount - amount;
    }

    @Override
    public int remove(Aspect aspect, int amount) {
        int tempAmount = amount;
        amount -= transport.takeEssentia(aspect, amount, direction);
        if (amount == tempAmount)
            return ((IAspectContainer) transport).takeFromContainer(aspect, amount) ? amount : 0;
        return amount;
    }

    @Override
    public CoordSet getCoordSet() {
        return coordSet;
    }
}
