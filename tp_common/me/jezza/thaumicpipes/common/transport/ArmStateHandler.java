package me.jezza.thaumicpipes.common.transport;

import java.util.ArrayList;

import me.jezza.thaumicpipes.api.interfaces.IThaumicPipe;
import me.jezza.thaumicpipes.client.core.NodeState;
import me.jezza.thaumicpipes.common.core.utils.CoordSet;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

public class ArmStateHandler {

    private ForgeDirection priority = ForgeDirection.UNKNOWN;
    private ArmState[] armStateArray;

    public ArmStateHandler() {
        armStateArray = new ArmState[6];
    }

    public NodeState updateArmStates(IThaumicPipe pipe, World world, CoordSet coordSet) {
        int index = 0;
        for (ForgeDirection direction : ForgeDirection.VALID_DIRECTIONS)
            armStateArray[index++] = new ArmState(direction, coordSet.copy().addForgeDirection(direction).getTileEntity(world), pipe.canConnectTo(direction));
        return createNode();
    }

    public ArmState[] getArmStateArray() {
        return armStateArray;
    }

    public ArmState getArmState(ForgeDirection direction) {
        return armStateArray[direction.ordinal()];
    }

    public void resetPriority() {
        priority = ForgeDirection.UNKNOWN;
    }

    public void cyclePriorityState() {
        ArrayList<ForgeDirection> validDirections = new ArrayList<ForgeDirection>();
        validDirections.add(ForgeDirection.UNKNOWN);

        for (int i = 0; i < armStateArray.length; i++) {
            ArmState currentState = armStateArray[i];
            if (currentState != null && currentState.isValid())
                validDirections.add(currentState.getDirection());
        }

        int direction = priority.ordinal();
        do {
            if (++direction == 7)
                direction = 0;
        } while (!validDirections.contains(ForgeDirection.getOrientation(direction)));

        priority = ForgeDirection.getOrientation(direction);
    }

    public boolean isPriority(ForgeDirection direction) {
        return direction == priority;
    }

    public NodeState createNode() {
        return NodeState.createNodeState(armStateArray);
    }
}
