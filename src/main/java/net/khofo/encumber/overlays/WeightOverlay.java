package net.khofo.encumber.overlays;

import net.khofo.encumber.configs.Configs;
import net.khofo.encumber.events.WeightEvent;
import net.minecraft.client.Minecraft;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraftforge.client.event.RenderGuiOverlayEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class WeightOverlay {
    @SubscribeEvent
    public static void onRenderGuiOverlay(RenderGuiOverlayEvent.Post event) {
        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.player != null) {
            GuiGraphics guiGraphics = event.getGuiGraphics();
            PoseStack poseStack = guiGraphics.pose();
            double weight = WeightEvent.calculateWeight(minecraft.player);
            String weightText = "Weight: " + weight + "/" + WeightEvent.getThreshold(Configs.SLOWNESS_5_THRESHOLD);

            // Render the weight text on the screen
            poseStack.pushPose();
            RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);

            // Specify the position where the text should be rendered
            int x = 5; // X position on the screen
            int y = 5; // Y position on the screen

            Font font = minecraft.font;

            if(weight < WeightEvent.getThreshold(Configs.SLOWNESS_3_THRESHOLD)){
                font.drawInBatch(weightText, x, y, 0xFFFFFF, false, poseStack.last().pose(), guiGraphics.bufferSource(), Font.DisplayMode.NORMAL, 0, 15728880);
            }

            if(weight >= WeightEvent.getThreshold(Configs.SLOWNESS_3_THRESHOLD) &&weight < WeightEvent.getThreshold(Configs.SLOWNESS_5_THRESHOLD)){
                font.drawInBatch(weightText, x, y, 0xE9CF11, false, poseStack.last().pose(), guiGraphics.bufferSource(), Font.DisplayMode.NORMAL, 0, 15728880);
            }

            if(weight >= WeightEvent.getThreshold(Configs.SLOWNESS_5_THRESHOLD)){
                font.drawInBatch(weightText, x, y, 0xE30C0C, false, poseStack.last().pose(), guiGraphics.bufferSource(), Font.DisplayMode.NORMAL, 0, 15728880);
            }


            poseStack.popPose();
        }
    }
}
