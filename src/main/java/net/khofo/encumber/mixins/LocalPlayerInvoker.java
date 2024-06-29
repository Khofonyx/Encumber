package net.khofo.encumber.mixins;

import net.minecraft.client.player.LocalPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(LocalPlayer.class)
public interface LocalPlayerInvoker {
    @Invoker("canStartSprinting")
    boolean invokeCanStartSprinting();
}
