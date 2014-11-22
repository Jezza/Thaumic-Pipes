package me.jezza.thaumicpipes.common.transport.connection;

import codechicken.lib.vec.Cuboid6;
import me.jezza.thaumicpipes.common.core.PipeProperties;
import me.jezza.thaumicpipes.common.core.interfaces.IOcclusionPart;
import net.minecraftforge.common.util.ForgeDirection;

public enum NodeState implements IOcclusionPart {
    BIG_NODE(true, true),
    NORMAL_NODE(true, false),
    DIRECTIONAL_SECTION(false, false);

    private final boolean isNode, bigNode;
    private ForgeDirection direction = ForgeDirection.UNKNOWN;
    private Cuboid6 occlusionBox = PipeProperties.getNode();

    private NodeState(boolean isNode, boolean bigNode) {
        this.isNode = isNode;
        this.bigNode = bigNode;
    }

    public NodeState setDirectionalSection(int side) {
        direction = ForgeDirection.getOrientation(side);
        occlusionBox = PipeProperties.getSmallNode(direction.ordinal());
        return this;
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

    public static NodeState createNodeState(ArmState[] armStateArray) {
        boolean[] flags = new boolean[6];

        for (int i = 0; i < armStateArray.length; i++) {
            ArmState armState = armStateArray[i];
            boolean flag = armState.isPartValid();
            if (flag && !armState.isPipe())
                return BIG_NODE;
            flags[i] = flag;
        }

        int side = 0;
        int count = 0;

        for (int i = 0; i <= 5; i += 2) {
            boolean firstValid = flags[i];
            boolean secondValid = flags[i + 1];

            if (firstValid)
                count++;
            if (secondValid)
                count++;
            if (firstValid && secondValid)
                side = i;
        }

        if (count != 2)
            return NORMAL_NODE;

        return DIRECTIONAL_SECTION.setDirectionalSection(side);
    }

}
