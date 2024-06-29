package net.khofo.encumber.mixins;

import net.khofo.encumber.accessors.LivingEntityAccessor;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.material.Fluid;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(LivingEntity.class)
public abstract class MixinLivingEntity implements LivingEntityAccessor {

    @Shadow
    protected abstract void jumpInLiquid(TagKey<Fluid> fluidTag);

    @Override
    public void callJumpInLiquid(TagKey<Fluid> fluidTag) {
        this.jumpInLiquid(fluidTag);
    }
}