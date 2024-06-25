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
import net.minecraftforge.common.capabilities.ForgeCapabilities;
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
            if (!player.isSpectator() && !player.isCreative()) {
                if (calculateWeight(player) > getThreshold(Configs.JUMPING_THRESHOLD)) {
                    player.setDeltaMovement(0, 0, 0);
                    player.hasImpulse = true;
                }
            }
        }
    }

    public static double calculateWeight(Player player) {
        double totalWeight = 0.0;

        List<ItemStack> inventory = new ArrayList<>();
        inventory.addAll(player.getInventory().items);
        inventory.add(player.getOffhandItem());
        inventory.addAll(iterableToList(player.getArmorSlots()));

        for (ItemStack stack : inventory) {
            ResourceLocation itemId = BuiltInRegistries.ITEM.getKey(stack.getItem());
            double itemWeight = Encumber.getItemWeight(itemId);
            totalWeight += itemWeight * stack.getCount();
            List<String> containerNames = Configs.CONTAINERS.get();
            for (String containerName : containerNames) {
                if (itemId.toString().equals(containerName)) {
                    totalWeight += calculateContainerWeight(stack);
                }
            }
        }
        return totalWeight;
    }

    public static double calculateContainerWeight(ItemStack containerStack) {
        final double[] containerWeight = {0.0};

        // Check if the item stack has an inventory (is a container)
        containerStack.getCapability(ForgeCapabilities.ITEM_HANDLER).ifPresent(handler -> {
            for (int i = 0; i < handler.getSlots(); i++) {
                ItemStack stack = handler.getStackInSlot(i);
                if (!stack.isEmpty()) {
                    ResourceLocation itemId = BuiltInRegistries.ITEM.getKey(stack.getItem());
                    double itemWeight = Encumber.getItemWeight(itemId);
                    containerWeight[0] += itemWeight * stack.getCount();

                    // Check if this item is also a container and calculate its contents' weight recursively
                    List<String> containerNames = Configs.CONTAINERS.get();
                    for (String containerName : containerNames) {
                        if (itemId.toString().equals(containerName)) {
                            containerWeight[0] += calculateContainerWeight(stack);
                        }
                    }
                }
            }
        });

        return containerWeight[0];
    }



    private static void applyEffectsBasedOnWeight(Player player, double weight) {
        if (weight > getThreshold(Configs.FALL_FLYING_THRESHOLD) && getThreshold(Configs.FALL_FLYING_THRESHOLD) > -1) {
            player.stopFallFlying();
        }
        if (weight > getThreshold(Configs.RIDING_THRESHOLD) && getThreshold(Configs.RIDING_THRESHOLD) > -1) {
            player.stopRiding();
        }
        //if (weight > getThreshold(Configs.SLOWNESS_1_THRESHOLD) && getThreshold(Configs.SLOWNESS_1_THRESHOLD) > -1) {
           // player.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 6, 1, false, false, false));
        //}
        if (weight > getThreshold(Configs.SLOWNESS_3_THRESHOLD) && getThreshold(Configs.SLOWNESS_3_THRESHOLD) > -1) {
            player.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 6, 2,false,false,false));
        }
        if (weight > getThreshold(Configs.SLOWNESS_5_THRESHOLD) && getThreshold(Configs.SLOWNESS_5_THRESHOLD) > -1) {
            player.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 6, 4,false,false,false));
        }
        if (weight > getThreshold(Configs.JUMPING_THRESHOLD) && getThreshold(Configs.JUMPING_THRESHOLD) > -1) {
            // Note this won't work past 1.20.4 since they removed allowing negative amplifier values.
            //player.addEffect(new MobEffectInstance(MobEffects.JUMP, 6, -6,false,false,false));
        }
    }

    public static double getThreshold(ForgeConfigSpec.ConfigValue<Double> config) {
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