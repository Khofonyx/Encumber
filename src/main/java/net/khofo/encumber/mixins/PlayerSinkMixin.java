package net.khofo.encumber.mixins;

import net.khofo.encumber.configs.Configs;
import net.khofo.encumber.events.WeightEvent;
import net.minecraft.client.Minecraft;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.ForgeMod;
import net.minecraftforge.common.extensions.IForgeLivingEntity;
import net.minecraftforge.fluids.FluidType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LivingEntity.class)
public class PlayerSinkMixin {
    static {
        System.out.println("PlayerSinkMixin loaded");
    }

    @Inject(method = "jumpInLiquid(Lnet/minecraft/tags/TagKey;)V", at = @At("HEAD"), cancellable = true, remap = false)
    private void jumpInLiquid(TagKey<Fluid> pFluidTag, CallbackInfo ci) {
        if ((Object) this instanceof Player player) {
            double weight = WeightEvent.calculateWeight(player);
            double maxCarryWeight = WeightEvent.getWeightWithBoostItem(player,1);

            if (weight > maxCarryWeight) {
                boolean spacebarPressed = Minecraft.getInstance().options.keyJump.isDown();
                if (spacebarPressed){
                    player.setDeltaMovement(new Vec3(0.0D, (double)-0.08F * player.getAttributeValue(ForgeMod.SWIM_SPEED.get()), 0.0D));
                }else{
                    player.setDeltaMovement(new Vec3(0.0D, (double)-0.04F * player.getAttributeValue(ForgeMod.SWIM_SPEED.get()), 0.0D));
                }
                ci.cancel();
            } else {
                // Normal behavior
                player.setDeltaMovement(player.getDeltaMovement().add(0.0D, (double)0.04F * player.getAttributeValue(ForgeMod.SWIM_SPEED.get()), 0.0D));
            }
        }
    }
}
