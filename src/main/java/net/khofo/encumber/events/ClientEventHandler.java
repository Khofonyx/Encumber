package net.khofo.encumber.events;

import net.khofo.encumber.UIElements.WeightEditScreen;
import net.khofo.encumber.configs.CommonConfigs;
import net.khofo.encumber.events.WeightEvent;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.Options;
import net.minecraft.client.player.LocalPlayer;
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
                if (WeightEvent.getThresholdTF(CommonConfigs.LOCK_GUI_TO_CREATIVE)){
                    LocalPlayer player = Minecraft.getInstance().player;
                    if(player.isCreative()){
                        Minecraft.getInstance().setScreen(new WeightEditScreen(Component.literal("Edit Item Weights")));
                    }else{
                        System.out.println("Player not in creative mode, failed to open GUI");
                    }
                }else{
                    Minecraft.getInstance().setScreen(new WeightEditScreen(Component.literal("Edit Item Weights")));
                }
            }
        }
    }
}