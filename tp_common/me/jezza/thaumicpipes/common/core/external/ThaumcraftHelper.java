package me.jezza.thaumicpipes.common.core.external;

import me.jezza.thaumicpipes.common.core.external.ConnectionRegistry.Priority;
import me.jezza.thaumicpipes.common.interfaces.IConnectionRegister;
import me.jezza.thaumicpipes.common.interfaces.IThaumicPipe;
import net.minecraft.tileentity.TileEntity;
import thaumcraft.common.tiles.TileAlembic;
import thaumcraft.common.tiles.TileJarFillable;
import thaumcraft.common.tiles.TileThaumatorium;
import thaumcraft.common.tiles.TileThaumatoriumTop;

public class ThaumcraftHelper implements IConnectionRegister {

    @Override
    public void init() {
        ConnectionRegistry.registerSource(Priority.NORMAL, TileAlembic.class);
        ConnectionRegistry.registerSource(Priority.LOWEST, TileJarFillable.class);

        ConnectionRegistry.registerRequester(Priority.NORMAL, TileThaumatorium.class);
        ConnectionRegistry.registerRequester(Priority.NORMAL, TileThaumatoriumTop.class);
        ConnectionRegistry.registerRequester(Priority.LOWEST, TileJarFillable.class);
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

}
