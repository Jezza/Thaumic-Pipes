package me.jezza.thaumicpipes.common.core.utils;

import net.minecraft.util.MathHelper;

public class TimeTickerF {

    private float amount, upper, lower, stepAmount;

    public TimeTickerF(float amount, float upper, float lower) {
        this.amount = MathHelper.clamp_float(amount, lower, upper);
        this.upper = upper;
        this.lower = lower;
        this.stepAmount = 1.0F;
    }

    public TimeTickerF(float amount, float upper) {
        this.amount = MathHelper.clamp_float(amount, lower, upper);
        this.upper = upper;
        this.lower = amount;
        this.stepAmount = 1.0F;
    }

    public TimeTickerF setStepAmount(float stepAmount) {
        this.stepAmount = stepAmount;
        return this;
    }

    public boolean tick() {
        if ((amount += stepAmount) >= upper)
            amount = lower;
        return amount == lower;
    }

    public float getAmount() {
        return amount;
    }
}
