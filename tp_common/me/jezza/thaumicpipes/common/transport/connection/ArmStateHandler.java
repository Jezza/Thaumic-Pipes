package me.jezza.thaumicpipes.common.transport.connection;

import me.jezza.oc.api.network.interfaces.INetworkNode;
import me.jezza.thaumicpipes.common.core.interfaces.IThaumicPipe;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.util.ForgeDirection;
import thaumcraft.api.aspects.IEssentiaTransport;

import java.util.ArrayList;
import java.util.Collection;

public class ArmStateHandler {

    private ArmState[] armStateArray;

    private Collection<INetworkNode> pipes;
    private Collection<IEssentiaTransport> inputs, storages, outputs;

    public ArmStateHandler() {
        ForgeDirection[] directions = ForgeDirection.VALID_DIRECTIONS;
        armStateArray = new ArmState[6];
        armStateArray[0] = new ArmState(directions[0]);
        armStateArray[1] = new ArmState(directions[1]);
        armStateArray[2] = new ArmState(directions[2]);
        armStateArray[3] = new ArmState(directions[3]);
        armStateArray[4] = new ArmState(directions[4]);
        armStateArray[5] = new ArmState(directions[5]);

        pipes = new ArrayList<INetworkNode>();
        inputs = new ArrayList<IEssentiaTransport>();
        storages = new ArrayList<IEssentiaTransport>();
        outputs = new ArrayList<IEssentiaTransport>();
    }

    public NodeState updateArmStates(IThaumicPipe pipe, TileEntity[] tileEntities) {
        ForgeDirection[] directions = ForgeDirection.VALID_DIRECTIONS;

        pipes.clear();
        inputs.clear();
        storages.clear();
        outputs.clear();

        for (int i = 0; i <= 5; i++) {
            ForgeDirection direction = directions[i];
            TileEntity tileEntity = tileEntities[i];

            boolean isValidConnection = tileEntity != null && pipe.canConnectTo(tileEntity, direction);
            ArmState armState = armStateArray[i].update(direction, tileEntity, isValidConnection);
            if (!armState.isPartValid())
                continue;
            int connectionType = armState.getConnectionType();

            if (ConnectionType.isPipe(connectionType))
                pipes.add((INetworkNode) ((IThaumicPipe) tileEntity).getPipe());

            else if (ConnectionType.isInput(connectionType))
                inputs.add((IEssentiaTransport) tileEntity);

            else if (ConnectionType.isStorage(connectionType))
                storages.add((IEssentiaTransport) tileEntity);

            else if (ConnectionType.isOutput(connectionType))
                outputs.add((IEssentiaTransport) tileEntity);
        }

        return createNode();
    }

    public Collection<INetworkNode> getValidConnections() {
        return pipes;
    }

    public Collection<IEssentiaTransport> getInputs() {
        return inputs;
    }

    public Collection<IEssentiaTransport> getStorage() {
        return storages;
    }

    public Collection<IEssentiaTransport> getOutputs() {
        return outputs;
    }

    public ArmState[] getArmStateArray() {
        return armStateArray;
    }

    private NodeState createNode() {
        return NodeState.createNodeState(armStateArray);
    }
}
