package net.khofo.encumber.mixins;

import net.khofo.encumber.configs.CommonConfigs;
import net.khofo.encumber.events.WeightEvent;
import net.minecraft.client.player.LocalPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LocalPlayer.class)
public abstract class PlayerMixin {

    static {
        System.out.println("PlayerMixin loaded");
    }

    @Inject(method = "canStartSprinting", at = @At("HEAD"), cancellable = true)
    private void onCanStartSprinting(CallbackInfoReturnable<Boolean> cir) {
        LocalPlayer player = (LocalPlayer) (Object) this;
        if(!player.isCreative() && !player.isSpectator()){
            double playerWeight = WeightEvent.calculateWeight(player);
            double boostItemWeight = WeightEvent.getWeightWithBoostItem(player, 0);
            double threshold_multiplier = WeightEvent.getThreshold(CommonConfigs.ENCUMBERED_THRESHOLD_MULTIPLIER);
            if (playerWeight >= boostItemWeight && threshold_multiplier > 0) {
                cir.setReturnValue(false);
            }
        }
    }
}