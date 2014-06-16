package me.jezza.thaumicpipes.common;

import me.jezza.thaumicpipes.common.items.ItemFocusWrench;
import me.jezza.thaumicpipes.common.lib.Strings;
import net.minecraft.item.Item;

public class ModItems {

    public static Item focusWrench;

    public static void init() {
        focusWrench = new ItemFocusWrench(Strings.WRENCH_FOCUS);
    }

}
