package me.jezza.thaumicpipes.common.core.external;

import me.jezza.thaumicpipes.common.interfaces.IThaumicPipe;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.util.ForgeDirection;
import thaumcraft.api.aspects.IEssentiaTransport;
import thaumcraft.api.nodes.INode;
import thaumcraft.common.tiles.TileAlembic;
import thaumcraft.common.tiles.TileJarFillable;
import thaumcraft.common.tiles.TileThaumatorium;
import thaumcraft.common.tiles.TileThaumatoriumTop;
import thaumcraft.common.tiles.TileTube;

public class ThaumcraftHelper {

    public static boolean isValidConnection(TileEntity tileEntity, ForgeDirection direction) {
        if (isMatch(tileEntity)) {
            if (isJarFillable(tileEntity))
                return true;

            if (tileEntity instanceof IEssentiaTransport)
                return ((IEssentiaTransport) tileEntity).isConnectable(direction.getOpposite());
            return true;
        }
        return false;
    }

    private static boolean isMatch(TileEntity tileEntity) {

        boolean flag = ModHelper.isThaumicTinkererLoaded();

        if (flag)
            flag = ThaumicTinkerHelper.isRepairer(tileEntity);

        return isComponent(tileEntity) || isSource(tileEntity) || shouldSupply(tileEntity) || flag;
    }

    public static boolean isSource(TileEntity tileEntity) {
        return isAlembic(tileEntity);
    }

    public static boolean shouldSupply(TileEntity tileEntity) {
        return isAlchemicalConstruct(tileEntity);
    }

    public static boolean isComponent(TileEntity tileEntity) {
        return isJarFillable(tileEntity) || isThaumicPipe(tileEntity);
    }

    public static boolean isEssentiaTube(TileEntity tileEntity) {
        return tileEntity instanceof TileTube;
    }

    public static boolean isThaumicPipe(TileEntity tileEntity) {
        return tileEntity instanceof IThaumicPipe;
    }

    public static boolean isJarFillable(TileEntity tileEntity) {
        return tileEntity instanceof TileJarFillable;
    }

    public static boolean isAlembic(TileEntity tileEntity) {
        return tileEntity instanceof TileAlembic;
    }

    public static boolean isAlchemicalConstruct(TileEntity tileEntity) {
        return tileEntity instanceof TileThaumatorium || tileEntity instanceof TileThaumatoriumTop;
    }

    public static boolean isNode(TileEntity tileEntity) {
        return tileEntity instanceof INode;
    }
}
