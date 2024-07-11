package net.khofo.encumber.events;

import net.khofo.encumber.Encumber;
import net.khofo.encumber.configs.Configs;
import net.khofo.encumber.accessors.LivingEntityAccessor;
import net.khofo.encumber.mixins.LocalPlayerInvoker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.animal.Pig;
import net.minecraft.world.entity.animal.horse.AbstractHorse;
import net.minecraft.world.entity.animal.horse.Llama;
import net.minecraft.world.entity.monster.Strider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.vehicle.AbstractMinecart;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.ForgeMod;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.EventPriority;
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
                if(getThresholdTF(Configs.RIDING_FLYING_JUMPING_TIED_TO_OVER_ENCUMBERED_THRESHOLD)){
                    if (calculateWeight(player) >= getWeightWithBoostItem(player,1)) {
                        player.setDeltaMovement(0, 0, 0);
                        player.hasImpulse = true;
                    }
                }else{
                    if (calculateWeight(player) >= getThreshold(Configs.JUMPING_THRESHOLD)) {
                        player.setDeltaMovement(0, 0, 0);
                        player.hasImpulse = true;
                    }
                }
            }
        }
    }

    public static double getWeightWithBoostItem(Player player, int slownessLevel){
        double retWeight = 0.0;
        if (slownessLevel == 0){
            retWeight = getThreshold(Configs.ENCUMBERED_THRESHOLD) + getBoostItemAmount(player);
        }
        if(slownessLevel == 1){
            retWeight = getThreshold(Configs.OVER_ENCUMBERED_THRESHOLD) + getBoostItemAmount(player);
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
                if (getThresholdTF(Configs.ALLOW_MULTIPLE_BOOST_ITEMS)){
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
        if (weight >= getWeightWithBoostItem(player, 1) && getThreshold(Configs.OVER_ENCUMBERED_THRESHOLD) > -1) {
            player.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 6, 4, false, false, false));
            if (getThresholdTF(Configs.RIDING_FLYING_JUMPING_TIED_TO_OVER_ENCUMBERED_THRESHOLD)) {
                player.stopFallFlying();
                //player.stopRiding();
                if ((player.isInWater() || player.isInLava()) && getThresholdTF(Configs.SINK_IN_WATER_LAVA)){
                    boolean spacebarPressed = Minecraft.getInstance().options.keyJump.isDown();
                    if (spacebarPressed){
                        player.setDeltaMovement(new Vec3(0.0D, (double)-0.08F * player.getAttributeValue(ForgeMod.SWIM_SPEED.get()), 0.0D));
                    }else{
                        player.setDeltaMovement(new Vec3(0.0D, (double)-0.04F * player.getAttributeValue(ForgeMod.SWIM_SPEED.get()), 0.0D));
                    }
                }
            }
        } else if (weight >= getWeightWithBoostItem(player, 0) && getThreshold(Configs.ENCUMBERED_THRESHOLD) > -1) {
            //player.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 6, 1, false, false, false));
            if (player instanceof LocalPlayer) {
                LocalPlayer localPlayer = (LocalPlayer) player;
                boolean canSprint = ((LocalPlayerInvoker) localPlayer).invokeCanStartSprinting();
                if (!canSprint) {
                    localPlayer.setSprinting(false);
                }
            }
        }

        if(!getThresholdTF(Configs.RIDING_FLYING_JUMPING_TIED_TO_OVER_ENCUMBERED_THRESHOLD)){
            if (weight >= getThreshold(Configs.FALL_FLYING_THRESHOLD) && getThreshold(Configs.FALL_FLYING_THRESHOLD) > -1) {
                player.stopFallFlying();
            }
            // Mounting is handled in OnEntityInteract
        }
    }

    @SubscribeEvent
    public void onEntityInteract(PlayerInteractEvent.EntityInteract event) {
        Entity target = event.getTarget();
        Player player = event.getEntity();

        // Check if the entity can be ridden
        if (!player.isSpectator() && !player.isCreative()) {
            if (canBeRidden(target)) {
                if (getThresholdTF(Configs.RIDING_FLYING_JUMPING_TIED_TO_OVER_ENCUMBERED_THRESHOLD)) {
                    if (calculateWeight(player) >= getWeightWithBoostItem(player, 1) && getThreshold(Configs.OVER_ENCUMBERED_THRESHOLD) > -1) {
                        if (isMountingAttempt(target, player, event.getHand())) {
                            if (!event.isCanceled()) { // Check if the event has not been canceled already
                                event.setCanceled(true); // Cancel the event to prevent mounting
                            }
                        }
                        if (player.isPassenger()){

                        }
                    }
                } else {
                    if (calculateWeight(player) >= getThreshold(Configs.RIDING_THRESHOLD) && getThreshold(Configs.RIDING_THRESHOLD) > -1) {
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