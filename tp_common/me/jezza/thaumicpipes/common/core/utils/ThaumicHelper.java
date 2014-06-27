package me.jezza.thaumicpipes.common.core.utils;

import me.jezza.thaumicpipes.common.interfaces.IThaumicPipe;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.IFluidHandler;
import thaumcraft.api.aspects.IAspectContainer;
import thaumcraft.api.aspects.IAspectSource;
import thaumcraft.api.aspects.IEssentiaTransport;
import thaumcraft.api.nodes.INode;

public class ThaumicHelper {

    public static boolean isValidConnection(TileEntity tileEntity, ForgeDirection direction) {
        if (tileEntity instanceof IFluidHandler)
            return false;

        if (isJar(tileEntity) || isPipe(tileEntity) || isContainer(tileEntity))
            return true;

        if (tileEntity instanceof IEssentiaTransport)
            return ((IEssentiaTransport) tileEntity).isConnectable(direction.getOpposite());
        return false;
    }

    public static boolean isMatch(TileEntity tileEntity) {
        return isPipe(tileEntity) || isJar(tileEntity) || isContainer(tileEntity);
    }

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
        return tileEntity instanceof INode;
    }

    public static boolean isAlchemicalConstruct(TileEntity tileEntity) {
        return tileEntity instanceof IAspectContainer && tileEntity instanceof IEssentiaTransport && tileEntity instanceof ISidedInventory;
    }
}
