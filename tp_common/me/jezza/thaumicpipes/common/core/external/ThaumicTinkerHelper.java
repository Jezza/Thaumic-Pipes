package me.jezza.thaumicpipes.common.core.external;

import me.jezza.thaumicpipes.api.AbstractRegistry;
import vazkii.tinkerer.common.block.tile.TileRepairer;

public class ThaumicTinkerHelper extends AbstractRegistry {

    @Override
    public void init() {
        register();

        registerRequester(TileRepairer.class);
    }
}
