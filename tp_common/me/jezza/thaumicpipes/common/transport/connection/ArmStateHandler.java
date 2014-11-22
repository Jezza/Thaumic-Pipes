package me.jezza.thaumicpipes.common.transport.connection;

import me.jezza.thaumicpipes.common.core.interfaces.IThaumicPipe;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.util.ForgeDirection;

public class ArmStateHandler {

    private final ArmState CACHE = new ArmState();
    private ArmState[] armStateArray;

    public ArmStateHandler() {
        armStateArray = new ArmState[6];
    }

    public NodeState updateArmStates(IThaumicPipe pipe, TileEntity[] tileEntities) {
        ForgeDirection[] directions = ForgeDirection.VALID_DIRECTIONS;
        for (int i = 0; i <= 5; i++) {
            ForgeDirection direction = directions[i];
            TileEntity tileEntity = tileEntities[i];
            boolean isValidConnection = pipe.canConnectTo(tileEntity, direction);
            armStateArray[i] = createArmState(direction, tileEntity, isValidConnection);
        }

        return createNode();
    }

    private ArmState createArmState(ForgeDirection direction, TileEntity tileEntity, boolean isValidConnection) {
        ArmState armState;
        try {
            armState = (ArmState) CACHE.clone();
        } catch (CloneNotSupportedException e) {
            armState = new ArmState(direction, tileEntity, isValidConnection);
        }

        return armState.setFields(direction, tileEntity, isValidConnection);
    }


    public ArmState[] getArmStateArray() {
        return armStateArray;
    }

    public NodeState createNode() {
        return NodeState.createNodeState(armStateArray);
    }
}
