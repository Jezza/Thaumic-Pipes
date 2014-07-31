package me.jezza.thaumicpipes.common.transport.connection;

import codechicken.lib.vec.Cuboid6;
import me.jezza.thaumicpipes.common.multipart.pipe.PipeProperties;
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
        for (ArmState armState : armStateArray)
            if (armState != null && armState.isValid() && !armState.getEntry().getType().isPipe())
                return new NodeState(true, true, 0);

        int side = 0;
        int count = 0;
        boolean isNode = true;

        for (int i = 0; i <= 5; i += 2) {
            ArmState firstState = armStateArray[i];
            ArmState secondState = armStateArray[i + 1];

            boolean firstValid = firstState.isValid();
            boolean secondValid = secondState.isValid();

            if (firstValid)
                count++;
            if (secondValid)
                count++;

            if (firstValid && secondValid) {
                side = i;
                isNode = !firstState.isOppositeSide(secondState);
            }
        }

        if (count != 2)
            isNode = true;

        return new NodeState(isNode, false, side);
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

    public Cuboid6 getOcclusionBox() {
        return isNode ? PipeProperties.getNode() : PipeProperties.getSlimedNode(direction.ordinal());
    }

}
