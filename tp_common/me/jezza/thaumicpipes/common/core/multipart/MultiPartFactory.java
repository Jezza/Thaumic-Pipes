package me.jezza.thaumicpipes.common.core.multipart;

import java.util.Arrays;

import me.jezza.thaumicpipes.common.ModBlocks;
import me.jezza.thaumicpipes.common.core.utils.CoordSet;
import me.jezza.thaumicpipes.common.tileentity.TileThaumicPipe;
import net.minecraft.block.Block;
import net.minecraft.world.World;
import codechicken.lib.vec.BlockCoord;
import codechicken.multipart.MultiPartRegistry;
import codechicken.multipart.MultipartGenerator;
import codechicken.multipart.MultiPartRegistry.IPartConverter;
import codechicken.multipart.MultiPartRegistry.IPartFactory;
import codechicken.multipart.TMultiPart;

public class MultiPartFactory implements IPartFactory, IPartConverter {

    public static final String thaumicPipe = "tp_thaumicPipe";

    public void init() {
        MultiPartRegistry.registerConverter(this);
        MultiPartRegistry.registerParts(this, new String[] { thaumicPipe });

        MultipartGenerator.registerPassThroughInterface("me.jezza.thaumicpipes.common.interfaces.IThaumicPipe");
    }

    @Override
    public Iterable<Block> blockTypes() {
        return Arrays.asList(ModBlocks.thaumicPipe);
    }

    @Override
    public TMultiPart convert(World world, BlockCoord pos) {
        CoordSet coordSet = new CoordSet(pos);
        if (coordSet.isThaumicPipe(world))
            return new ThaumicPipePart((TileThaumicPipe) coordSet.getTileEntity(world));
        return null;
    }

    @Override
    public TMultiPart createPart(String name, boolean client) {
        if (thaumicPipe.equals(name))
            return new ThaumicPipePart();
        return null;
    }
}
