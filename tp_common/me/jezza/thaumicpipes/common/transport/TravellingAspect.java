package me.jezza.thaumicpipes.common.transport;

import java.util.ArrayList;
import java.util.LinkedHashSet;

import me.jezza.thaumicpipes.common.core.utils.CoordSet;

public class TravellingAspect {

    private CoordSet finalSet;
    private ArrayList<CoordSet> mapList;

    public TravellingAspect(ArrayList<CoordSet> mapList) {
        this.mapList = mapList;
        finalSet = mapList.get(mapList.size() - 1);
    }

    public boolean isNextLocation(CoordSet coordSet) {
        if (mapList.isEmpty())
            return false;

        CoordSet nextSet = mapList.get(0);
        return nextSet.equals(coordSet);
    }

    public boolean movedTo(CoordSet coordSet) {
        mapList = (ArrayList<CoordSet>) mapList.subList(mapList.indexOf(coordSet), mapList.size() - 1);
        return true;
    }

    public boolean isAtDestination(CoordSet coordSet) {
        return mapList.isEmpty() && coordSet.equals(finalSet);
    }

}
