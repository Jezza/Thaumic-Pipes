package me.jezza.thaumicpipes.common.transport.connection;

import me.jezza.thaumicpipes.common.core.interfaces.IThaumicPipe;
import me.jezza.thaumicpipes.common.transport.wrappers.EssentiaTransportWrapper;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.util.ForgeDirection;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

public class ArmStateHandler {

    private final ArmState[] armStateArray;

    private final Map<ForgeDirection, IThaumicPipe> pipes;
    private final List<EssentiaTransportWrapper> inputs, storages, outputs;

    public ArmStateHandler() {
        ForgeDirection[] directions = ForgeDirection.VALID_DIRECTIONS;
        armStateArray = new ArmState[6];
        armStateArray[0] = new ArmState(directions[0]);
        armStateArray[1] = new ArmState(directions[1]);
        armStateArray[2] = new ArmState(directions[2]);
        armStateArray[3] = new ArmState(directions[3]);
        armStateArray[4] = new ArmState(directions[4]);
        armStateArray[5] = new ArmState(directions[5]);

        pipes = new EnumMap<>(ForgeDirection.class);
        inputs = new ArrayList<>(6);
        storages = new ArrayList<>(6);
        outputs = new ArrayList<>(6);
    }

    public NodeState updateArmStates(IThaumicPipe pipe, TileEntity[] tileEntities) {
        pipes.clear();
        inputs.clear();
        storages.clear();
        outputs.clear();

        for (int i = 0; i <= 5; i++) {
            ForgeDirection direction = ForgeDirection.VALID_DIRECTIONS[i];
            TileEntity tileEntity = tileEntities[i];

            boolean isValidConnection = tileEntity != null && pipe.canConnectTo(tileEntity, direction);
            ArmState armState = armStateArray[i].update(direction, tileEntity, isValidConnection);
            if (!armState.isPartValid() || tileEntity == null)
                continue;
            int connectionType = armState.getConnectionType();

            if (ConnectionType.isPipe(connectionType))
                pipes.put(direction, ((IThaumicPipe) tileEntity).getPipe());

            else if (ConnectionType.isInput(connectionType))
                inputs.add(new EssentiaTransportWrapper(pipe, tileEntity, direction));

            else if (ConnectionType.isStorage(connectionType))
                storages.add(new EssentiaTransportWrapper(pipe, tileEntity, direction));

            else if (ConnectionType.isOutput(connectionType))
                outputs.add(new EssentiaTransportWrapper(pipe, tileEntity, direction, true));
        }

        return createNode();
    }

    public Map<ForgeDirection, IThaumicPipe> getValidConnections() {
        return pipes;
    }

    public List<EssentiaTransportWrapper> getInputs() {
        return inputs;
    }

    public List<EssentiaTransportWrapper> getStorage() {
        return storages;
    }

    public List<EssentiaTransportWrapper> getOutputs() {
        return outputs;
    }

    public ArmState[] getArmStateArray() {
        return armStateArray;
    }

    private NodeState createNode() {
        return NodeState.createNodeState(armStateArray);
    }
}
