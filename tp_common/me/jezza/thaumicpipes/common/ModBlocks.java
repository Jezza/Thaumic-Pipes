package me.jezza.thaumicpipes.common;

import me.jezza.thaumicpipes.common.blocks.BlockThaumicPipe;
import me.jezza.thaumicpipes.common.lib.Strings;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;

public class ModBlocks {

    public static Block thaumicPipe;

    public static void init() {
        thaumicPipe = new BlockThaumicPipe(Material.anvil, Strings.THAUMIC_PIPE);
    }

}
