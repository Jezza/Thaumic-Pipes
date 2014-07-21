package me.jezza.thaumicpipes.common.transport.connection;

import java.util.ArrayList;
import java.util.List;

import me.jezza.thaumicpipes.api.interfaces.IThaumicPipe;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.util.ForgeDirection;

public class ArmStateHandler {

    private ForgeDirection priority = ForgeDirection.UNKNOWN;
    private ArmState[] armStateArray;

    public ArmStateHandler() {
        armStateArray = new ArmState[6];
    }

    public NodeState updateArmStates(IThaumicPipe pipe, TileEntity[] tileEntities) {
        int index = 0;
        for (ForgeDirection direction : ForgeDirection.VALID_DIRECTIONS) {
            TileEntity tileEntity = tileEntities[index];
            boolean isValidConnection = tileEntity != null ? pipe.canConnectTo(direction) : false;
            armStateArray[index++] = new ArmState(direction, tileEntity, isValidConnection);
        }
        return createNode();
    }

    public ArmState[] getArmStateArray() {
        return armStateArray;
    }

    public List<ArmState> getPipeConnections() {
        ArrayList<ArmState> armList = new ArrayList<ArmState>();
        for (ArmState armState : armStateArray)
            if (armState != null && armState.isValid() && armState.isPipe())
                armList.add(armState);
        return armList;
    }

    public List<ArmState> getSourceConnections() {
        ArrayList<ArmState> armList = new ArrayList<ArmState>();
        for (ArmState armState : armStateArray)
            if (armState != null && armState.isValid() && armState.getEntry().getType().isSource())
                armList.add(armState);
        return armList;
    }

    public List<ArmState> getRequesterConnections() {
        ArrayList<ArmState> armList = new ArrayList<ArmState>();
        for (ArmState armState : armStateArray)
            if (armState != null && armState.isValid() && armState.getEntry().getType().isRequester())
                armList.add(armState);
        return armList;
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
