package net.khofo.encumber.overlays;

import net.khofo.encumber.configs.Configs;
import net.khofo.encumber.events.WeightEvent;
import net.minecraft.client.Minecraft;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraftforge.client.event.RenderGuiOverlayEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class WeightOverlay {
    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onRenderGuiOverlay(RenderGuiOverlayEvent.Post event) {
        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.player != null && !minecraft.player.isCreative() && !minecraft.player.isSpectator() && !(minecraft.screen instanceof InventoryScreen)) {
            GuiGraphics guiGraphics = event.getGuiGraphics();
            PoseStack poseStack = guiGraphics.pose();
            double weight = WeightEvent.calculateWeight(minecraft.player);
            String weightText = "Weight: " + weight + "/" + WeightEvent.getWeightWithBoostItem(minecraft.player,5);

            // Render the weight text on the screen
            poseStack.pushPose();
            RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);

            // Get screen dimensions
            int screenWidth = minecraft.getWindow().getGuiScaledWidth();
            int screenHeight = minecraft.getWindow().getGuiScaledHeight();

            // Specify the position where the text should be rendered
            int x = (screenWidth / 2) + (int)WeightEvent.getThreshold(Configs.WEIGHT_UI_X_OFFSET); // Center x position
            int y = screenHeight - 49 - (int)WeightEvent.getThreshold(Configs.WEIGHT_UI_Y_OFFSET); // Adjust y position to be above the armor bars (you can tweak this value)

            Font font = minecraft.font;

            // Calculate the width of the text to center it properly
            int textWidth = font.width(weightText);

            if(weight < WeightEvent.getWeightWithBoostItem(minecraft.player,3)){
                font.drawInBatch(weightText, x - textWidth / 2, y, 0xFFFFFF, true, poseStack.last().pose(), guiGraphics.bufferSource(), Font.DisplayMode.NORMAL, 0, 15728880);
            }

            if(weight >= WeightEvent.getWeightWithBoostItem(minecraft.player,3) && weight < WeightEvent.getWeightWithBoostItem(minecraft.player,5)){
                font.drawInBatch(weightText, x - textWidth / 2, y, 0xE9CF11, true, poseStack.last().pose(), guiGraphics.bufferSource(), Font.DisplayMode.SEE_THROUGH, 0, 15728880);
            }

            if(weight >= WeightEvent.getWeightWithBoostItem(minecraft.player,5)){
                font.drawInBatch(weightText, x - textWidth / 2, y, 0xE30C0C, true, poseStack.last().pose(), guiGraphics.bufferSource(), Font.DisplayMode.NORMAL, 0, 15728880);
            }

            poseStack.popPose();
        }
    }
}
