package me.jezza.thaumicpipes.common.core.external;

import me.jezza.thaumicpipes.api.AbstractRegistry;
import me.jezza.thaumicpipes.api.registry.Priority;
import thaumcraft.common.tiles.TileAlembic;
import thaumcraft.common.tiles.TileJarFillable;
import thaumcraft.common.tiles.TileThaumatorium;
import thaumcraft.common.tiles.TileThaumatoriumTop;

public class ThaumcraftHelper extends AbstractRegistry {

    @Override
    public void init() {
        register();

        registerSource(TileAlembic.class, Priority.NORMAL, 0.5F);
        registerSource(TileJarFillable.class, Priority.LOWEST);

        registerRequester(TileThaumatorium.class);
        registerRequester(TileThaumatoriumTop.class);
        registerRequester(TileJarFillable.class, Priority.LOWEST);
    }
}
