package me.jezza.thaumicpipes.client.core;

import me.jezza.thaumicpipes.common.transport.ArmState;
import net.minecraftforge.common.util.ForgeDirection;

public class NodeState {

    private boolean isNode = false;
    private boolean bigNode = false;
    private ForgeDirection direction = ForgeDirection.UNKNOWN;

    private NodeState(boolean isNode, boolean bigNode, int side) {
        this.isNode = isNode || bigNode;
        this.bigNode = bigNode;

        if (!isNode)
            direction = ForgeDirection.getOrientation(side);
    }

    public static NodeState createNodeState(ArmState[] armStateArray) {
        boolean isNode = true;
        int count = 0;
        int side = 0;

        for (int i = 0; i <= 5; i += 2) {
            ArmState firstState = armStateArray[i];
            ArmState secondState = armStateArray[i + 1];

            boolean firstValid = firstState.isValid();
            boolean secondValid = secondState.isValid();

            if (firstValid)
                count++;
            if (secondValid)
                count++;

            if (firstValid && secondValid)
                side = i;

            if (confirmArmState(firstState, secondState))
                isNode = false;
        }

        if (count != 2)
            isNode = true;

        boolean bigNode = false;
        if (isNode)
            for (ArmState armState : armStateArray) {
                if (armState == null)
                    continue;

                if (armState.getType().isBigNode()) {
                    bigNode = true;
                    break;
                }
            }

        return new NodeState(isNode, bigNode, side);
    }

    private static boolean confirmArmState(ArmState firstState, ArmState secondState) {
        return firstState.isValid() && secondState.isValid() && firstState.getDirection().getOpposite().equals(secondState.getDirection());
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
