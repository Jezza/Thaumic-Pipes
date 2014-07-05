package me.jezza.thaumicpipes.common.core.utils;

import me.jezza.thaumicpipes.common.core.TPLogger;
import net.minecraft.util.MathHelper;

public class TimeTicker {

    private int amount, upper, lower;

    public TimeTicker(int startingAmount, int upper, int lower) {
        amount = MathHelper.clamp_int(startingAmount, lower, upper);
        this.upper = upper - 1;
        this.lower = lower;
    }

    public TimeTicker(int startingAmount, int upper) {
        amount = MathHelper.clamp_int(startingAmount, lower, upper);
        this.upper = upper - 1;
        this.lower = amount;
    }

    public boolean tick() {
        if (amount++ >= upper)
            amount = lower;
        return amount == lower;
    }

    public int getAmount() {
        return amount;
    }
}
