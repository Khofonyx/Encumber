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
                if (calculateWeight(player) >= getThreshold(Configs.JUMPING_THRESHOLD)) {
                    player.setDeltaMovement(0, 0, 0);
                    player.hasImpulse = true;
                }
            }
        }
    }
    public static double getWeightWithBoostItem(Player player, int slownessLevel){
        double retWeight = 0.0;
        if (slownessLevel == 3){
            retWeight = getThreshold(Configs.SLOWNESS_3_THRESHOLD) + getBoostItemAmount(player);
        }
        if(slownessLevel == 5){
            retWeight = getThreshold(Configs.SLOWNESS_5_THRESHOLD) + getBoostItemAmount(player);
        }
        return retWeight;
    }

    public static double getBoostItemAmount(Player player) {
        double maxBoostAmount = 0.0;
        List<String> boostItems = Configs.BOOST_ITEMS.get();
        List<Double> boostAmounts = Configs.BOOST_AMOUNT.get();
        Boolean allowMultipleBoostItems = getThresholdTF(Configs.ALLOW_MULTIPLE_BOOST_ITEMS);

        if (allowMultipleBoostItems) {
            // Check inventory, armor, and offhand for multiple boost items
            for (ItemStack stack : player.getInventory().items) {
                maxBoostAmount += getBoostAmount(stack, boostItems, boostAmounts);
            }
            for (ItemStack stack : player.getInventory().armor) {
                maxBoostAmount += getBoostAmount(stack, boostItems, boostAmounts);
            }
            maxBoostAmount += getBoostAmount(player.getOffhandItem(), boostItems, boostAmounts);

            // If using Curios API, check curios slots as well
            // TODO: Add Curios slot checking logic here if applicable
        } else {
            // Check for the boost item providing the greatest boost amount
            for (ItemStack stack : player.getInventory().items) {
                double boostAmount = getBoostAmount(stack, boostItems, boostAmounts);
                if (boostAmount > maxBoostAmount) {
                    maxBoostAmount = boostAmount;
                }
            }
            for (ItemStack stack : player.getInventory().armor) {
                double boostAmount = getBoostAmount(stack, boostItems, boostAmounts);
                if (boostAmount > maxBoostAmount) {
                    maxBoostAmount = boostAmount;
                }
            }
            double offhandBoostAmount = getBoostAmount(player.getOffhandItem(), boostItems, boostAmounts);
            if (offhandBoostAmount > maxBoostAmount) {
                maxBoostAmount = offhandBoostAmount;
            }

            // If using Curios API, check curios slots as well
            // TODO: Add Curios slot checking logic here if applicable
        }
        return maxBoostAmount;
    }

    private static double getBoostAmount(ItemStack stack, List<String> boostItems, List<Double> boostAmounts) {
        ResourceLocation itemId = BuiltInRegistries.ITEM.getKey(stack.getItem());
        for (int i = 0; i < boostItems.size(); i++) {
            if (itemId != null && itemId.toString().equals(boostItems.get(i))) {
                return boostAmounts.get(i);
            }
        }
        return 0.0;
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
        if (weight >= getWeightWithBoostItem(player,5) && getThreshold(Configs.SLOWNESS_5_THRESHOLD) > -1) {
            player.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 6, 4,false,false,false));
        }else if (weight >= getWeightWithBoostItem(player,3) && getThreshold(Configs.SLOWNESS_3_THRESHOLD) > -1) {
            player.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 6, 2,false,false,false));
        }

        if (weight >= getThreshold(Configs.FALL_FLYING_THRESHOLD) && getThreshold(Configs.FALL_FLYING_THRESHOLD) > -1) {
            player.stopFallFlying();
        }
        if (weight >= getThreshold(Configs.RIDING_THRESHOLD) && getThreshold(Configs.RIDING_THRESHOLD) > -1) {
            player.stopRiding();
        }
    }

    public static double getThreshold(ForgeConfigSpec.ConfigValue<Double> config) {
        return config.get() != null ? config.get() : 0.0D;
    }

    public static Boolean getThresholdTF(ForgeConfigSpec.ConfigValue<Boolean> config) {
        return config.get() != null ? config.get() : false;
    }



    private static <T> List<T> iterableToList(Iterable<T> iterable) {
        List<T> list = new ArrayList<>();
        for (T item : iterable) {
            list.add(item);
        }
        return list;
    }
}