package me.jezza.thaumicpipes.common.core.multipart.pipe;

import me.jezza.thaumicpipes.common.core.multipart.ItemMultiPartAbstract;
import me.jezza.thaumicpipes.common.core.multipart.pipe.thaumic.ThaumicPipePart;
import me.jezza.thaumicpipes.common.core.utils.CoordSet;
import net.minecraft.block.Block;
import net.minecraft.block.Block.SoundType;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
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
    public void playPlacedSound(EntityPlayer player, World world, CoordSet coordSet) {
        SoundType type = Block.soundTypeStone;
        world.playSoundEffect(coordSet.getX() + 0.5F, coordSet.getY() + 0.5F, coordSet.getZ() + 0.5F, type.func_150496_b(), (type.getVolume() + 1.0F) / 2.0F, type.getPitch() * 0.8F);
    }

}
