package me.jezza.thaumicpipes.client.core;

import net.minecraftforge.common.util.ForgeDirection;

public class NodeState {

    private boolean isNode = false;
    private boolean bigNode = false;
    private ForgeDirection direction = ForgeDirection.UNKNOWN;

    public NodeState(boolean isNode, boolean bigNode, int side) {
        this.isNode = isNode || bigNode;
        this.bigNode = bigNode;

        if (!isNode)
            direction = ForgeDirection.getOrientation(side);
    }

    public boolean isNode() {
        return isNode;
    }

    public boolean isBigNode() {
        return bigNode;
    }

    public ForgeDirection getDirection() {
        return direction;
    }

}
