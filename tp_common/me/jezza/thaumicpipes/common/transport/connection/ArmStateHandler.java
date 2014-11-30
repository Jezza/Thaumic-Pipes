package me.jezza.thaumicpipes.common.transport.connection;

import me.jezza.oc.api.network.interfaces.INetworkNode;
import me.jezza.thaumicpipes.common.core.interfaces.IThaumicPipe;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.util.ForgeDirection;

import java.util.Collection;
import java.util.LinkedList;

public class ArmStateHandler {

    private final LinkedList<TileEntity> LINKED_LIST_CACHE = new LinkedList<>();
    private final ArmState ARM_STATE_CACHE = new ArmState();
    private ArmState[] armStateArray;

    private Collection<INetworkNode> pipeConnections;
    private Collection<TileEntity> jarConnections, albemicConnections, constructConnections, containerConnections;

    public ArmStateHandler() {
        armStateArray = new ArmState[6];
        pipeConnections = new LinkedList<>();
        jarConnections = createLinkedList();
        albemicConnections = createLinkedList();
        constructConnections = createLinkedList();
        containerConnections = createLinkedList();
    }

    private LinkedList<TileEntity> createLinkedList() {
        LinkedList<TileEntity> list;
        try {
            list = (LinkedList<TileEntity>) LINKED_LIST_CACHE.clone();
        } catch (Exception e) {
            list = new LinkedList<>();
        }
        return list;
    }

    public NodeState updateArmStates(IThaumicPipe pipe, TileEntity[] tileEntities) {
        ForgeDirection[] directions = ForgeDirection.VALID_DIRECTIONS;
        for (int i = 0; i <= 5; i++) {
            ForgeDirection direction = directions[i];
            TileEntity tileEntity = tileEntities[i];
            boolean isValidConnection = pipe.canConnectTo(tileEntity, direction);
            armStateArray[i] = createArmState(direction, tileEntity, isValidConnection);
        }

        updateConnections(tileEntities);


        return createNode();
    }

    private ArmState createArmState(ForgeDirection direction, TileEntity tileEntity, boolean isValidConnection) {
        ArmState armState;
        try {
            armState = (ArmState) ARM_STATE_CACHE.clone();
        } catch (CloneNotSupportedException e) {
            armState = new ArmState(direction, tileEntity, isValidConnection);
        }

        return armState.setFields(direction, tileEntity, isValidConnection);
    }

    private void updateConnections(TileEntity[] tileEntities) {
        albemicConnections.clear();
        jarConnections.clear();
        constructConnections.clear();
        containerConnections.clear();
        pipeConnections.clear();
        for (int i = 0; i <= 5; i++) {
            ArmState armState = armStateArray[i];
            if (armState.isPartValid()) {
                ConnectionType connectionType = armState.getConnectionType();
                TileEntity tileEntity = tileEntities[i];
                switch (connectionType) {
                    case DEFAULT:
                        break;
                    case ALBEMIC:
                        albemicConnections.add(tileEntity);
                        break;
                    case JAR:
                        jarConnections.add(tileEntity);
                        break;
                    case CONSTRUCT:
                        constructConnections.add(tileEntity);
                        break;
                    case ASPECT_CONTAINER:
                        containerConnections.add(tileEntity);
                        break;
                    case PIPE:
                        pipeConnections.add((INetworkNode) ((IThaumicPipe) tileEntity).getPipe());
                        break;
                }
            }
        }
    }

    public Collection<INetworkNode> getValidConnections() {
        return pipeConnections;
    }

    public Collection<TileEntity> getJarConnections() {
        return jarConnections;
    }

    public Collection<TileEntity> getContainerConnections() {
        return containerConnections;
    }

    public Collection<TileEntity> getAlbemicConnections() {
        return albemicConnections;
    }

    public Collection<TileEntity> getConstructConnections() {
        return constructConnections;
    }

    public ArmState[] getArmStateArray() {
        return armStateArray;
    }

    private NodeState createNode() {
        return NodeState.createNodeState(armStateArray);
    }
}
