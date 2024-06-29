package net.khofo.encumber.overlays;

import net.khofo.encumber.Encumber;
import net.khofo.encumber.configs.Configs;
import net.khofo.encumber.events.WeightEvent;
import net.minecraft.client.Minecraft;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.client.event.RenderGuiOverlayEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class WeightOverlay {

    private static final ResourceLocation ANVIL_ICON_WHITE = new ResourceLocation(Encumber.MOD_ID, "textures/gui/anvil_icon_white.png");
    private static final ResourceLocation ANVIL_ICON_YELLOW = new ResourceLocation(Encumber.MOD_ID, "textures/gui/anvil_icon_yellow.png");
    private static final ResourceLocation ANVIL_ICON_RED = new ResourceLocation(Encumber.MOD_ID, "textures/gui/anvil_icon_red.png");

    public static boolean isMenuOpen() {
        Minecraft minecraft = Minecraft.getInstance();
        Screen currentScreen = minecraft.screen;
        return currentScreen != null;
    }
    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onRenderGuiOverlay(RenderGuiOverlayEvent.Post event) {
        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.player != null && !minecraft.player.isCreative() && !minecraft.player.isSpectator()) {
            GuiGraphics guiGraphics = event.getGuiGraphics();
            PoseStack poseStack = guiGraphics.pose();
            double weight = WeightEvent.calculateWeight(minecraft.player);
            String weightText = "Weight: " + weight + "/" + Math.abs(WeightEvent.getWeightWithBoostItem(minecraft.player, 1));

            // Render the weight text on the screen
            poseStack.pushPose();
            RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);

            // Get screen dimensions
            int screenWidth = minecraft.getWindow().getGuiScaledWidth();
            int screenHeight = minecraft.getWindow().getGuiScaledHeight();

            // Specify the position where the text should be rendered
            // Adjust these offsets to position the UI element as desired
            double xOffsetPercentage = (WeightEvent.getThreshold(Configs.WEIGHT_UI_X_OFFSET)/100.0); // 50% from the left (centered horizontally)
            double yOffsetPercentage = (WeightEvent.getThreshold(Configs.WEIGHT_UI_Y_OFFSET)/100.0); // 5% from the top (near the bottom of the screen)
            //System.out.println(xOffsetPercentage);
            //System.out.println(yOffsetPercentage);
            int x = (int) (screenWidth * xOffsetPercentage);
            int y = (int) (screenHeight * yOffsetPercentage);

            Font font = minecraft.font;

            ResourceLocation anvilIcon = ANVIL_ICON_WHITE;
            if (weight >= WeightEvent.getWeightWithBoostItem(minecraft.player, 0) && weight < WeightEvent.getWeightWithBoostItem(minecraft.player, 1)) {
                anvilIcon = ANVIL_ICON_YELLOW;
            } else if (weight >= WeightEvent.getWeightWithBoostItem(minecraft.player, 1)) {
                anvilIcon = ANVIL_ICON_RED;
            }

            // Render the anvil icon
            RenderSystem.setShader(GameRenderer::getPositionTexShader);
            RenderSystem.setShaderTexture(0, anvilIcon);
            RenderSystem.enableBlend();
            RenderSystem.blendFuncSeparate(770, 771, 1, 0);
            RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);

            int iconSize = 12; // Adjust the size of the icon if needed
            int icon_x = (int)(screenWidth * .5);
            int icon_y = (screenHeight -43);
            if(WeightEvent.getThresholdTF(Configs.TOGGLE_ANVIL_ICON)){
                guiGraphics.blit(anvilIcon,icon_x - (iconSize /2),icon_y - (iconSize /2), 0, 0, iconSize, iconSize, iconSize, iconSize);
            }


            RenderSystem.disableBlend();

            // Calculate the width of the text to center it properly
            int textWidth = font.width(weightText);

            // Render the text with different colors based on weight conditions
            // Get the current screen
            Screen currentScreen = Minecraft.getInstance().screen;
            // Check if the current screen is an instance of InventoryScreen
            if(WeightEvent.getThresholdTF(Configs.TOGGLE_WEIGHT_TEXT)){
                if(currentScreen instanceof InventoryScreen){
                    if (weight < WeightEvent.getWeightWithBoostItem(minecraft.player, 0)) {
                        font.drawInBatch(weightText, x - textWidth / 2, y, 0xFFFFFF, true, poseStack.last().pose(), guiGraphics.bufferSource(), Font.DisplayMode.NORMAL, 0, 15728880);
                    } else if (weight >= WeightEvent.getWeightWithBoostItem(minecraft.player, 0) && weight < WeightEvent.getWeightWithBoostItem(minecraft.player, 1)) {
                        font.drawInBatch(weightText, x - textWidth / 2, y, 0xE9CF11, true, poseStack.last().pose(), guiGraphics.bufferSource(), Font.DisplayMode.NORMAL, 0, 15728880);
                    } else if (weight >= WeightEvent.getWeightWithBoostItem(minecraft.player, 1)) {
                        font.drawInBatch(weightText, x - textWidth / 2, y, 0xE30C0C, true, poseStack.last().pose(), guiGraphics.bufferSource(), Font.DisplayMode.NORMAL, 0, 15728880);
                    }
                }
            }

            poseStack.popPose();
        }
    }
}
