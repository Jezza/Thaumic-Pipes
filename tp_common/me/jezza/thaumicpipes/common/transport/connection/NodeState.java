package me.jezza.thaumicpipes.common.transport.connection;

import codechicken.lib.vec.Cuboid6;
import me.jezza.thaumicpipes.common.core.PipeProperties;
import me.jezza.thaumicpipes.common.core.interfaces.IOcclusionPart;
import net.minecraftforge.common.util.ForgeDirection;

public class NodeState implements IOcclusionPart, Cloneable {
    public static final NodeState BIG_NODE = new NodeState(0, true, true);
    public static final NodeState NORMAL_NODE = new NodeState(1, true, false);
    public static final NodeState DIRECTIONAL_SECTION = new NodeState(2, false, false);

    private final int id;
    private final boolean isNode, bigNode;
    private Cuboid6 occlusionBox = PipeProperties.getNode();
    private ForgeDirection direction = ForgeDirection.UNKNOWN;

    private NodeState(int id, boolean isNode, boolean bigNode) {
        this.id = id;
        this.isNode = isNode;
        this.bigNode = bigNode;
    }

    public NodeState setDirectionalSection(int side) {
        direction = ForgeDirection.getOrientation(side);
        occlusionBox = PipeProperties.getSmallNode(direction.ordinal());
        return this;
    }

    public int getId() {
        return id;
    }

    public ForgeDirection getDirection() {
        return direction;
    }

    @Override
    public boolean isPartValid() {
        return true;
    }

    @Override
    public Cuboid6 getOcclusionBox() {
        return occlusionBox;
    }

    @Override
    public final NodeState clone() {
        try {
            return (NodeState) super.clone();
        } catch (CloneNotSupportedException ignored) {
        }
        return new NodeState(id, isNode, bigNode);
    }

    public static NodeState createNodeState(ArmState[] armStateArray) {
        boolean[] flags = new boolean[6];

        for (int i = 0; i < armStateArray.length; i++) {
            ArmState armState = armStateArray[i];
            boolean flag = armState.isPartValid();
            if (flag && !armState.isPipe())
                return BIG_NODE.clone();
            flags[i] = flag;
        }

        int count = 0;
        for (boolean anArray : flags)
            if (anArray)
                count++;
        if (count != 2)
            return NORMAL_NODE.clone();

        int side = -1;
        boolean flag;

        for (int i = 0; i <= 5; i += 2) {
            flag = flags[i] && flags[i + 1];

            if (flag) {
                side = i;
                break;
            }
        }

        return side == -1 ? NORMAL_NODE.clone() : DIRECTIONAL_SECTION.clone().setDirectionalSection(side);
    }
}
