package me.jezza.thaumicpipes.common;

import me.jezza.thaumicpipes.common.core.multipart.pipe.ItemThaumicPipe;
import me.jezza.thaumicpipes.common.lib.Strings;
import net.minecraft.item.Item;

public class ModItems {

    public static Item thaumicPipe;

    public static void init() {
        thaumicPipe = new ItemThaumicPipe(Strings.ITEM_THAUMIC_PIPE);
    }

}
