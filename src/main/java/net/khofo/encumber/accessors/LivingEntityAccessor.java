package net.khofo.encumber.accessors;

import net.minecraft.tags.TagKey;
import net.minecraft.world.level.material.Fluid;

public interface LivingEntityAccessor {
    void callJumpInLiquid(TagKey<Fluid> fluidTag);
}