package net.khofo.encumber.events;

import net.khofo.encumber.overlays.WeightEditScreen;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.Options;
import net.minecraft.network.chat.Component;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import org.apache.commons.lang3.ArrayUtils;
import org.lwjgl.glfw.GLFW;

@Mod.EventBusSubscriber(modid = "encumber", bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ClientEventHandler {
    // Define the key mapping
    private static final KeyMapping OPEN_WEIGHT_EDIT_SCREEN_KEY = new KeyMapping(
            "key.encumber.open_weight_edit_screen",
            GLFW.GLFW_KEY_G,
            "key.categories.encumber"
    );

    @SubscribeEvent
    public static void onClientSetup(FMLClientSetupEvent event) {
        // Register the key binding
        Options options = Minecraft.getInstance().options;
        options.keyMappings = ArrayUtils.addAll(options.keyMappings, OPEN_WEIGHT_EDIT_SCREEN_KEY);
    }

    @Mod.EventBusSubscriber(modid = "encumber", value = Dist.CLIENT)
    public static class KeyInputHandler {
        @SubscribeEvent
        public static void onKeyInput(InputEvent.Key event) {
            if (OPEN_WEIGHT_EDIT_SCREEN_KEY.consumeClick()) {
                Minecraft.getInstance().setScreen(new WeightEditScreen(Component.literal("Edit Item Weights")));
            }
        }
    }
}