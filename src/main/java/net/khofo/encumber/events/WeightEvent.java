package net.khofo.encumber.events;

import net.khofo.encumber.Encumber;
import net.khofo.encumber.configs.Configs;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import java.util.ArrayList;
import java.util.List;

public class WeightEvent {

    @SubscribeEvent
    public void encumber(TickEvent.PlayerTickEvent event) {
        Player player = event.player;
        if (!player.isSpectator() && !player.isCreative()) {
            double weight = calculateWeight(player);
            applyEffectsBasedOnWeight(player, weight);
        }
    }

    @SubscribeEvent
    public void onLivingJumpEvent(LivingEvent.LivingJumpEvent event) {
        if (event.getEntity() instanceof Player) {
            Player player = (Player) event.getEntity();
            if (calculateWeight(player) > getThreshold(Configs.JUMPING_THRESHOLD)) {
                player.setDeltaMovement(0, 0, 0);
                player.hasImpulse = true;
            }
        }
    }

    private static double calculateWeight(Player player) {
        double totalWeight = 0.0;

        List<ItemStack> inventory = new ArrayList<>();
        inventory.addAll(player.getInventory().items);
        inventory.add(player.getOffhandItem());
        inventory.addAll(iterableToList(player.getArmorSlots()));

        for (ItemStack stack : inventory) {
            ResourceLocation itemId = BuiltInRegistries.ITEM.getKey(stack.getItem());
            double itemWeight = Encumber.getItemWeight(itemId);
            totalWeight += itemWeight * stack.getCount();
        }

        return totalWeight;
    }

    private static void applyEffectsBasedOnWeight(Player player, double weight) {
        if (weight > getThreshold(Configs.FALL_FLYING_THRESHOLD) && getThreshold(Configs.FALL_FLYING_THRESHOLD) > -1) {
            player.stopFallFlying();
        }
        if (weight > getThreshold(Configs.RIDING_THRESHOLD) && getThreshold(Configs.RIDING_THRESHOLD) > -1) {
            player.stopRiding();
        }
        if (weight > getThreshold(Configs.SLOWNESS_1_THRESHOLD) && getThreshold(Configs.SLOWNESS_1_THRESHOLD) > -1) {
            player.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 6, 0));
        }
        if (weight > getThreshold(Configs.SLOWNESS_2_THRESHOLD) && getThreshold(Configs.SLOWNESS_2_THRESHOLD) > -1) {
            player.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 6, 1));
        }
        if (weight > getThreshold(Configs.SLOWNESS_5_THRESHOLD) && getThreshold(Configs.SLOWNESS_5_THRESHOLD) > -1) {
            player.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 6, 4));
        }
        if (weight > getThreshold(Configs.JUMPING_THRESHOLD) && getThreshold(Configs.JUMPING_THRESHOLD) > -1) {
            // Note this won't work past 1.20.4 since they removed allowing negative amplifier values.
            player.addEffect(new MobEffectInstance(MobEffects.JUMP, 6, -6));
        }
    }

    private static double getThreshold(ForgeConfigSpec.ConfigValue<Double> config) {
        return config.get() != null ? config.get() : 0.0D;
    }

    private static <T> List<T> iterableToList(Iterable<T> iterable) {
        List<T> list = new ArrayList<>();
        for (T item : iterable) {
            list.add(item);
        }
        return list;
    }
}