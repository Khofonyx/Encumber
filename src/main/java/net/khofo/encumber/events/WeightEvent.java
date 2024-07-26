package net.khofo.encumber.events;

import net.khofo.encumber.Encumber;
import net.khofo.encumber.configs.CommonConfigs;
import net.khofo.encumber.mixins.LocalPlayerInvoker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.animal.Pig;
import net.minecraft.world.entity.animal.horse.AbstractHorse;
import net.minecraft.world.entity.animal.horse.Llama;
import net.minecraft.world.entity.monster.Strider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.vehicle.AbstractMinecart;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.ForgeMod;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
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
            if (player.isPassenger()) {
                // If player is riding something
                if (weight >= getWeightWithBoostItem(player, 1)) {
                    // If player is riding something then becomes overweight, dismount them
                    player.stopRiding();

                    // Ensure the player is placed above ground level
                    BlockPos currentPos = player.blockPosition();
                    BlockPos safePos = findSafePositionAbove(currentPos, player);

                    // Set the player's position to the safe position
                    player.setPos(safePos.getX() + 0.5, safePos.getY() + 0.5, safePos.getZ() + 0.5);
                }
            }
        }
    }

    private BlockPos findSafePositionAbove(BlockPos pos, Player player) {
        BlockPos newPos = pos.above();
        while (!player.getCommandSenderWorld().noCollision(player.getBoundingBox().move(0, newPos.getY() - pos.getY(), 0))) {
            newPos = newPos.above();
        }
        return newPos;
    }

   @SubscribeEvent
    public void onLivingJumpEvent(LivingEvent.LivingJumpEvent event) {
        if (event.getEntity() instanceof Player) {
            Player player = (Player) event.getEntity();
            if (!player.isSpectator() && !player.isCreative()) {
                if(getThresholdTF(CommonConfigs.RIDING_FLYING_JUMPING_TIED_TO_OVER_ENCUMBERED_THRESHOLD)){
                    if (calculateWeight(player) >= getWeightWithBoostItem(player,1)) {
                        player.setDeltaMovement(0, 0, 0);
                        player.hasImpulse = true;
                    }
                }else{
                    if (calculateWeight(player) >= getThreshold(CommonConfigs.JUMPING_THRESHOLD)) {
                        player.setDeltaMovement(0, 0, 0);
                        player.hasImpulse = true;
                    }
                }
            }
        }
    }

    public static double getWeightWithBoostItem(Player player, int slownessLevel) {
        double baseThreshold = getThreshold(CommonConfigs.OVER_ENCUMBERED_THRESHOLD);
        if (slownessLevel == 0) {
            baseThreshold = (baseThreshold + getBoostItemAmount(player)) * (getThreshold(CommonConfigs.ENCUMBERED_THRESHOLD_MULTIPLIER) / 100);
        } else if (slownessLevel == 1) {
            baseThreshold = getThreshold(CommonConfigs.OVER_ENCUMBERED_THRESHOLD) + getBoostItemAmount(player);
        }

        // Add the boost item amount to the base threshold
        double totalWeight = baseThreshold;

        // Get the player's leggings item
        ItemStack leggings = player.getItemBySlot(EquipmentSlot.LEGS);
        if (leggings.isEnchanted()) {
            // Check if the leggings have the Unencumberment enchantment
            int enchantmentLevel = EnchantmentHelper.getItemEnchantmentLevel(Encumber.UNENCUMBERMENT.get(), leggings);
            if (enchantmentLevel > 0) {
                // Apply the appropriate multiplier based on the enchantment level
                double multiplier = getCarryingCapacityBoost(enchantmentLevel);
                totalWeight *= multiplier;
            }
        }

        return totalWeight;
    }

    private static double getCarryingCapacityBoost(int level) {
        return switch (level) {
            case 1 -> CommonConfigs.UNENCUMBERMENT_LEVEL1_MULTIPLIER.get();
            case 2 -> CommonConfigs.UNENCUMBERMENT_LEVEL2_MULTIPLIER.get();
            case 3 -> CommonConfigs.UNENCUMBERMENT_LEVEL3_MULTIPLIER.get();
            default -> 1.0;
        };
    }

    public static double getBoostItemAmount(Player player) {
        double maxBoostAmount = 0.0;
        List<String> boostItems = CommonConfigs.BOOST_ITEMS.get();
        List<String> boostArmors = CommonConfigs.BOOST_ARMORS.get();
        List<Double> boostItemAmounts = CommonConfigs.BOOST_ITEMS_AMOUNT.get();
        List<Double> boostArmorAmounts = CommonConfigs.BOOST_ARMORS_AMOUNT.get();
        Boolean allowMultipleBoostItems = getThresholdTF(CommonConfigs.ALLOW_MULTIPLE_BOOST_ITEMS);

        if (allowMultipleBoostItems) {
            // Check inventory, armor, and offhand for multiple boost items
            for (ItemStack stack : player.getInventory().items) {
                maxBoostAmount += getBoostAmount(stack, boostItems, boostItemAmounts);
            }
            for (ItemStack stack : player.getInventory().armor) {
                maxBoostAmount += getBoostAmount(stack, boostItems, boostItemAmounts);
                maxBoostAmount += getBoostAmount(stack, boostArmors, boostArmorAmounts);
            }
            maxBoostAmount += getBoostAmount(player.getOffhandItem(), boostItems, boostItemAmounts);
            maxBoostAmount += getBoostAmount(player.getOffhandItem(), boostArmors, boostArmorAmounts);

            // If using Curios API, check curios slots as well
            // TODO: Add Curios slot checking logic here if applicable
        } else {
            // Check for the boost item providing the greatest boost amount
            for (ItemStack stack : player.getInventory().items) {
                double boostAmount = getBoostAmount(stack, boostItems, boostItemAmounts);
                if (boostAmount > maxBoostAmount) {
                    maxBoostAmount = boostAmount;
                }
            }
            for (ItemStack stack : player.getInventory().armor) {
                double boostAmount = getBoostAmount(stack, boostItems, boostItemAmounts);
                if (boostAmount > maxBoostAmount) {
                    maxBoostAmount = boostAmount;
                }

                double armorBoostAmount = getBoostAmount(stack, boostArmors, boostArmorAmounts);
                if (armorBoostAmount > maxBoostAmount) {
                    maxBoostAmount = armorBoostAmount;
                }

            }
            double offhandBoostAmount = getBoostAmount(player.getOffhandItem(), boostItems, boostItemAmounts);
            if (offhandBoostAmount > maxBoostAmount) {
                maxBoostAmount = offhandBoostAmount;
            }

            offhandBoostAmount = getBoostAmount(player.getOffhandItem(), boostArmors, boostArmorAmounts);
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
                if (getThresholdTF(CommonConfigs.ALLOW_MULTIPLE_BOOST_ITEMS)){
                    return boostAmounts.get(i) * stack.getCount();
                }else{
                    return boostAmounts.get(i);
                }
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
            List<String> containerNames = CommonConfigs.CONTAINERS.get();
            if (containerNames.contains(itemId.toString())){
                totalWeight += calculateContainerWeight(stack);
            }
        }
        return totalWeight;
    }

    public static double calculateContainerWeight(ItemStack containerStack) {
        Component displayNameComponent = containerStack.getDisplayName();
        String displayName = displayNameComponent.getString();
        System.out.println("Testing: " + displayName);
        final double[] containerWeight = {0.0};
        Screen currentScreen = Minecraft.getInstance().screen;
        System.out.println("Current Screen: " + currentScreen);
        // Check if the item stack has an inventory (is a container)
        containerStack.getCapability(ForgeCapabilities.ITEM_HANDLER).ifPresent(handler -> {
            System.out.println("Found Inventory with " +handler.getSlots() + " slots");
            for (int i = 0; i < handler.getSlots(); i++) {
                ItemStack stack = handler.getStackInSlot(i);
                if (!stack.isEmpty()) {
                    ResourceLocation itemId = BuiltInRegistries.ITEM.getKey(stack.getItem());
                    double itemWeight = Encumber.getItemWeight(itemId);
                    containerWeight[0] += itemWeight * stack.getCount();

                    // Check if this item is also a container and calculate its contents' weight recursively
                    List<String> containerNames = CommonConfigs.CONTAINERS.get();
                    if (containerNames.contains(itemId.toString())){
                        containerWeight[0] += calculateContainerWeight(stack);
                    }
                }
            }
        });
        return containerWeight[0];
    }

    private static void applyEffectsBasedOnWeight(Player player, double weight) {
        if (weight >= getWeightWithBoostItem(player, 1) && getThreshold(CommonConfigs.OVER_ENCUMBERED_THRESHOLD) > -1) {
            player.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 6, 4, false, false, false));
            if (getThresholdTF(CommonConfigs.RIDING_FLYING_JUMPING_TIED_TO_OVER_ENCUMBERED_THRESHOLD)) {
                player.stopFallFlying();
                if ((player.isInWater() || player.isInLava()) && getThresholdTF(CommonConfigs.SINK_IN_WATER_LAVA)){
                    boolean spacebarPressed = Minecraft.getInstance().options.keyJump.isDown();
                    if (spacebarPressed){
                        player.setDeltaMovement(new Vec3(0.0D, (double)-0.08F * player.getAttributeValue(ForgeMod.SWIM_SPEED.get()), 0.0D));
                    }else{
                        player.setDeltaMovement(new Vec3(0.0D, (double)-0.04F * player.getAttributeValue(ForgeMod.SWIM_SPEED.get()), 0.0D));
                    }
                }
            }
        } else if (weight >= getWeightWithBoostItem(player, 0) && getThreshold(CommonConfigs.ENCUMBERED_THRESHOLD_MULTIPLIER) > 0) {
            if (player instanceof LocalPlayer) {
                LocalPlayer localPlayer = (LocalPlayer) player;
                boolean canSprint = ((LocalPlayerInvoker) localPlayer).invokeCanStartSprinting();
                if (!canSprint) {
                    localPlayer.setSprinting(false);
                }
            }
        }

        if(!getThresholdTF(CommonConfigs.RIDING_FLYING_JUMPING_TIED_TO_OVER_ENCUMBERED_THRESHOLD)){
            if (weight >= getThreshold(CommonConfigs.FALL_FLYING_THRESHOLD) && getThreshold(CommonConfigs.FALL_FLYING_THRESHOLD) > -1) {
                player.stopFallFlying();
            }
        }
    }

    @SubscribeEvent
    public void onEntityInteract(PlayerInteractEvent.EntityInteract event) {
        Entity target = event.getTarget();
        Player player = event.getEntity();

        // Check if the entity can be ridden
        if (!player.isSpectator() && !player.isCreative()) {
            if (canBeRidden(target)) {
                if (getThresholdTF(CommonConfigs.RIDING_FLYING_JUMPING_TIED_TO_OVER_ENCUMBERED_THRESHOLD)) {
                    if (calculateWeight(player) >= getWeightWithBoostItem(player, 1) && getThreshold(CommonConfigs.OVER_ENCUMBERED_THRESHOLD) > -1) {
                        if (isMountingAttempt(target, player, event.getHand())) {
                            if (!event.isCanceled()) { // Check if the event has not been canceled already
                                event.setCanceled(true); // Cancel the event to prevent mounting
                            }
                        }
                        if (player.isPassenger()){

                        }
                    }
                } else {
                    if (calculateWeight(player) >= getThreshold(CommonConfigs.RIDING_THRESHOLD) && getThreshold(CommonConfigs.RIDING_THRESHOLD) > -1) {
                        if (isMountingAttempt(target, player, event.getHand())) {
                            if (!event.isCanceled()) { // Check if the event has not been canceled already
                                event.setCanceled(true); // Cancel the event to prevent mounting
                            }
                        }
                    }
                }
            }
        }
    }

    private boolean canBeRidden(Entity entity) {
        // Check if the entity can be ridden
        return entity instanceof AbstractMinecart ||
                entity instanceof AbstractHorse && ((AbstractHorse) entity).isSaddled()||
                entity instanceof Pig && ((Pig) entity).isSaddled() ||
                entity instanceof Llama ||
                entity instanceof Strider && ((Strider) entity).isSaddled();
    }

    private boolean isMountingAttempt(Entity target, Player player, InteractionHand hand) {
        if (target instanceof AbstractHorse) {
            AbstractHorse horse = (AbstractHorse) target;
            // Check if the player is holding a saddle or horse armor
            boolean isHoldingSaddle = !player.getItemInHand(hand).isEmpty() &&
                    player.getItemInHand(hand).getItem() instanceof net.minecraft.world.item.SaddleItem;
            return !isHoldingSaddle && !horse.isBaby();
        }

        if (target instanceof Pig) {
            Pig pig = (Pig) target;
            return pig.isSaddled();
        }

        if (target instanceof Strider) {
            Strider strider = (Strider) target;
            return strider.isSaddled();
        }

        if (target instanceof Llama) {
            Llama llama = (Llama) target;
            boolean isHoldingSaddle = !player.getItemInHand(hand).isEmpty() &&
                    player.getItemInHand(hand).getItem() instanceof net.minecraft.world.item.SaddleItem;
            return !isHoldingSaddle;
        }

        // For minecarts and other entities, assume right-clicking mounts them
        return true;
    }

    public static double getThreshold(ForgeConfigSpec.ConfigValue<Double> config) {
        return config.get() != null ? config.get() : 0.0D;
    }

    public static int getThresholdInt(ForgeConfigSpec.ConfigValue<Integer> config) {
        return config.get() != null ? config.get() : 0;
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