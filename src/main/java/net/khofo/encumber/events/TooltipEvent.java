package net.khofo.encumber.events;

import net.khofo.encumber.Encumber;
import net.khofo.encumber.configs.Configs;
import net.minecraft.ChatFormatting;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import java.util.Map;

import static net.khofo.encumber.Encumber.itemWeights;

public class TooltipEvent {
    @SubscribeEvent
    public void onItemTooltip(ItemTooltipEvent event) {
        ItemStack stack = event.getItemStack();
        ResourceLocation itemId = BuiltInRegistries.ITEM.getKey(stack.getItem());

        // Display item weight
        if (itemId != null && itemWeights.containsKey(itemId)) {
            double weight = itemWeights.get(itemId);
            MutableComponent weightText = Component.literal("Weight: " + weight).withStyle(ChatFormatting.GOLD);
            event.getToolTip().add(weightText);
        }
    }

    private double getCarryingCapacityBoost(int level) {
        return switch (level) {
            case 1 -> Configs.UNENCUMBERMENT_LEVEL1_MULTIPLIER.get();
            case 2 -> Configs.UNENCUMBERMENT_LEVEL2_MULTIPLIER.get();
            case 3 -> Configs.UNENCUMBERMENT_LEVEL3_MULTIPLIER.get();
            default -> 1.0;
        };
    }
}
