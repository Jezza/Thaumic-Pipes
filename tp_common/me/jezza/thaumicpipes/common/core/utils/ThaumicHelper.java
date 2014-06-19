package me.jezza.thaumicpipes.common.core.utils;

import me.jezza.thaumicpipes.common.interfaces.IThaumicPipe;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.tileentity.TileEntity;
import thaumcraft.api.aspects.IAspectContainer;
import thaumcraft.api.aspects.IAspectSource;
import thaumcraft.api.aspects.IEssentiaTransport;
import thaumcraft.api.nodes.INode;

public class ThaumicHelper {

    public static boolean isPipe(TileEntity tileEntity) {
        return tileEntity instanceof IThaumicPipe;
    }

    public static boolean isJar(TileEntity tileEntity) {
        return tileEntity instanceof IAspectSource;
    }

    public static boolean isContainer(TileEntity tileEntity) {
        return tileEntity instanceof IAspectContainer && !(tileEntity instanceof IAspectSource || tileEntity instanceof INode || tileEntity instanceof ISidedInventory);
    }

    public static boolean isNode(TileEntity tileEntity) {
        return tileEntity instanceof IAspectContainer && tileEntity instanceof INode;
    }

    public static boolean isAlchemicalConstruct(TileEntity tileEntity) {
        return tileEntity instanceof IAspectContainer && tileEntity instanceof IEssentiaTransport && tileEntity instanceof ISidedInventory;
    }
}
