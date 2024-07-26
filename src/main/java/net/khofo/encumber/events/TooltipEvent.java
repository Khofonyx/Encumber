package net.khofo.encumber.events;

import net.khofo.encumber.configs.CommonConfigs;
import net.minecraft.ChatFormatting;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import java.util.List;

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

        // Display boost item amount
        List<String> boostItems = CommonConfigs.BOOST_ITEMS.get();
        List<Double> boostItemAmounts = CommonConfigs.BOOST_ITEMS_AMOUNT.get();
        int boostItemIndex = boostItems.indexOf(itemId.toString());
        if (boostItemIndex != -1) {
            double boostAmount = boostItemAmounts.get(boostItemIndex);
            MutableComponent boostText = Component.literal("Boost Amount: " + boostAmount).withStyle(ChatFormatting.GREEN);
            event.getToolTip().add(boostText);
        }

        List<String> boostArmors = CommonConfigs.BOOST_ARMORS.get();
        List<Double> boostArmorAmounts = CommonConfigs.BOOST_ARMORS_AMOUNT.get();
        int boostArmorIndex = boostArmors.indexOf(itemId.toString());
        if (boostArmorIndex != -1) {
            double boostAmount = boostArmorAmounts.get(boostArmorIndex);
            MutableComponent boostText = Component.literal("Boost Amount: " + boostAmount).withStyle(ChatFormatting.GREEN);
            event.getToolTip().add(boostText);
        }
    }
}
