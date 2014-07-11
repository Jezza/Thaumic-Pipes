package me.jezza.thaumicpipes.common.multipart.pipe;

import me.jezza.thaumicpipes.common.multipart.ItemMultiPartAbstract;
import me.jezza.thaumicpipes.common.multipart.pipe.thaumic.ThaumicPipePart;
import net.minecraft.block.Block;
import net.minecraft.block.Block.SoundType;
import codechicken.multipart.TMultiPart;

public class ItemThaumicPipe extends ItemMultiPartAbstract {

    public ItemThaumicPipe(String name) {
        super(name);
    }

    @Override
    public TMultiPart getPart() {
        return new ThaumicPipePart();
    }

    @Override
    public SoundType getSoundType() {
        return Block.soundTypeStone;
    }

}
