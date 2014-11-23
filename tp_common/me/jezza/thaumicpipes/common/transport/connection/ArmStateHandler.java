package me.jezza.thaumicpipes.common.transport.connection;

import me.jezza.oc.api.interfaces.INetworkNode;
import me.jezza.thaumicpipes.common.core.interfaces.IThaumicPipe;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.util.ForgeDirection;

import java.util.Collection;
import java.util.LinkedList;

public class ArmStateHandler {

    private final ArmState CACHE = new ArmState();
    private ArmState[] armStateArray;

    private Collection<INetworkNode> validConnections;

    public ArmStateHandler() {
        armStateArray = new ArmState[6];
        validConnections = new LinkedList<>();
    }

    public NodeState updateArmStates(IThaumicPipe pipe, TileEntity[] tileEntities) {
        ForgeDirection[] directions = ForgeDirection.VALID_DIRECTIONS;
        for (int i = 0; i <= 5; i++) {
            ForgeDirection direction = directions[i];
            TileEntity tileEntity = tileEntities[i];
            boolean isValidConnection = pipe.canConnectTo(tileEntity, direction);
            armStateArray[i] = createArmState(direction, tileEntity, isValidConnection);
        }

        validConnections.clear();
        for (int i = 0; i <= 5; i++) {
            ArmState armState = armStateArray[i];
            if (armState.isPartValid() && armState.isPipe())
                validConnections.add((INetworkNode) ((IThaumicPipe) tileEntities[i]).getPipe());
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

    public Collection<INetworkNode> getValidConnections() {
        return validConnections;
    }

    public ArmState[] getArmStateArray() {
        return armStateArray;
    }

    public NodeState createNode() {
        return NodeState.createNodeState(armStateArray);
    }
}
