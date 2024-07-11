package net.khofo.encumber.events;

import net.minecraft.ChatFormatting;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import static net.khofo.encumber.Encumber.itemWeights;

public class TooltipEvent {
    @SubscribeEvent
    public void onItemTooltip(ItemTooltipEvent event) {
        ItemStack stack = event.getItemStack();
        ResourceLocation itemId = BuiltInRegistries.ITEM.getKey(stack.getItem());

        if (itemId != null && itemWeights.containsKey(itemId)) {
            double weight = itemWeights.get(itemId);
            MutableComponent weightText = Component.literal("Weight: " + weight).withStyle(ChatFormatting.GOLD);
            event.getToolTip().add(weightText);
        }
    }
}
