package me.jezza.thaumicpipes.common.core.interfaces;

import codechicken.lib.vec.Cuboid6;

public interface IOcclusionPart {

    public boolean isPartValid();

    public Cuboid6 getOcclusionBox();

}
