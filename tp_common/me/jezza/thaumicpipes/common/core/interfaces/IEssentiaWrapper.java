package me.jezza.thaumicpipes.common.core.interfaces;

import me.jezza.oc.common.utils.CoordSet;
import thaumcraft.api.aspects.Aspect;

public interface IEssentiaWrapper {
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
