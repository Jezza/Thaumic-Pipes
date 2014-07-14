package me.jezza.thaumicpipes.common.multipart;

import java.util.Arrays;

import net.minecraft.block.Block;
import net.minecraft.world.World;
import me.jezza.thaumicpipes.common.ModBlocks;
import me.jezza.thaumicpipes.common.multipart.pipe.thaumic.ThaumicPipePart;
import codechicken.lib.vec.BlockCoord;
import codechicken.multipart.MultiPartRegistry;
import codechicken.multipart.MultiPartRegistry.IPartConverter;
import codechicken.multipart.MultiPartRegistry.IPartFactory;
import codechicken.multipart.MultipartGenerator;
import codechicken.multipart.TMultiPart;

public class MultiPartFactory implements IPartFactory, IPartConverter {

    public static final String thaumicPipe = "tp_thaumicPipe";

    public void init() {
        MultiPartRegistry.registerConverter(this);
        MultiPartRegistry.registerParts(this, new String[] { thaumicPipe });

        MultipartGenerator.registerPassThroughInterface("me.jezza.thaumicpipes.common.interfaces.IThaumicPipe");
    }

    @Override
    public TMultiPart createPart(String name, boolean client) {
        if (thaumicPipe.equals(name))
            return new ThaumicPipePart();
        return null;
    }

    @Override
    public Iterable<Block> blockTypes() {
        return Arrays.asList(ModBlocks.thaumicPipe);
    }

    @Override
    public TMultiPart convert(World world, BlockCoord pos) {
        Block b = world.getBlock(pos.x, pos.y, pos.z);
        if (b == ModBlocks.thaumicPipe)
            return new ThaumicPipePart();
        return null;
    }
}
