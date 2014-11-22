package me.jezza.thaumicpipes.common.multipart.items;

import codechicken.multipart.TMultiPart;
import me.jezza.thaumicpipes.ThaumicPipes;
import me.jezza.thaumicpipes.common.multipart.core.ItemMultiPartAbstract;
import me.jezza.thaumicpipes.common.multipart.part.thaumic.ThaumicPipePart;
import net.minecraft.block.Block;
import net.minecraft.block.Block.SoundType;

public class ItemThaumicPipe extends ItemMultiPartAbstract {

    public ItemThaumicPipe(String name) {
        super(name);
        setCreativeTab(ThaumicPipes.creativeTab);
    }

    @Override
    public TMultiPart getPart() {
        return new ThaumicPipePart();
    }

    @Override
    public SoundType getSoundType() {
        return Block.soundTypeStone;
    }

    @Override
    public boolean requireIconReg() {
        return false;
    }
}
