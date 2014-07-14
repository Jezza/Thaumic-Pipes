package me.jezza.thaumicpipes.common.core.external;

import me.jezza.thaumicpipes.api.AbstractRegistry;
import me.jezza.thaumicpipes.api.interfaces.IThaumicPipe;
import me.jezza.thaumicpipes.api.registry.Priority;
import net.minecraft.tileentity.TileEntity;
import thaumcraft.common.tiles.TileAlembic;
import thaumcraft.common.tiles.TileJarFillable;
import thaumcraft.common.tiles.TileThaumatorium;
import thaumcraft.common.tiles.TileThaumatoriumTop;

public class ThaumcraftHelper extends AbstractRegistry {

    @Override
    public void init() {
        register();

        registerSource(TileAlembic.class, Priority.NORMAL, 0.5F);
        registerSource(TileJarFillable.class, Priority.LOWEST, 0.1F);

        registerRequester(TileThaumatorium.class, Priority.NORMAL, 0.0F);
        registerRequester(TileThaumatoriumTop.class, Priority.NORMAL, 0.0F);
        registerRequester(TileJarFillable.class, Priority.LOWEST, 0.1F);
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
