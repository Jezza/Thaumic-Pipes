package me.jezza.thaumicpipes.common.transport.wrappers;

import me.jezza.oc.common.utils.CoordSet;
import me.jezza.thaumicpipes.common.core.interfaces.IEssentiaWrapper;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;

public class AspectListWrapper implements IEssentiaWrapper {
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
