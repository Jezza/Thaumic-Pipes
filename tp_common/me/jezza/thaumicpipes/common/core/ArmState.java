package me.jezza.thaumicpipes.common.core;

import me.jezza.thaumicpipes.common.core.utils.ThaumicHelper;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.util.ForgeDirection;

public class ArmState {
    public ForgeDirection dir;
    // 0 - Container
    // 1 - Jar
    // 2 - Pipe
    public int connectionType;
    private float extensionSize;
    public boolean priority;

    public ArmState(ForgeDirection dir, TileEntity tileEntity, boolean canConnect, boolean priority) {
        this.dir = dir;
        this.priority = priority;
        if (canConnect) {
            if (ThaumicHelper.isPipe(tileEntity)) {
                connectionType = 2;
                extensionSize = 0.0F;
            } else if (ThaumicHelper.isJar(tileEntity)) {
                connectionType = 1;
                extensionSize = 0.1F;
            } else if (ThaumicHelper.isContainer(tileEntity)) {
                connectionType = 0;
                extensionSize = 0.5F;
            } else {
                connectionType = -1;
                extensionSize = -1.0F;
            }
        } else {
            connectionType = -1;
        }
    }

    public boolean isValid() {
        return connectionType >= 0;
    }

    public boolean isContainerType() {
        return connectionType == 0;
    }

    public boolean isJarType() {
        return connectionType == 1;
    }

    public boolean isPipeType() {
        return connectionType == 2;
    }

    public float getExtensionSize() {
        return extensionSize;
    }

}