package net.khofo.encumber.UIElements;

import net.khofo.encumber.Encumber;
import net.khofo.encumber.configs.ClientConfigs;
import net.khofo.encumber.events.WeightEvent;
import net.khofo.encumber.helpers.VanillaScreens;
import net.minecraft.client.Minecraft;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.*;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.RenderGuiOverlayEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import java.math.BigDecimal;
import java.math.RoundingMode;

@OnlyIn(Dist.CLIENT)
public class WeightOverlay {
    private static final ResourceLocation ANVIL_ICON_WHITE = new ResourceLocation(Encumber.MOD_ID, "textures/gui/anvil_icon_white.png");
    private static final ResourceLocation ANVIL_ICON_YELLOW = new ResourceLocation(Encumber.MOD_ID, "textures/gui/anvil_icon_yellow.png");
    private static final ResourceLocation ANVIL_ICON_RED = new ResourceLocation(Encumber.MOD_ID, "textures/gui/anvil_icon_red.png");
    public static final RandomSource random = RandomSource.create();
    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onRenderGuiOverlay(RenderGuiOverlayEvent.Post event) {
        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.player != null && !minecraft.player.isCreative() && !minecraft.player.isSpectator()) {
            GuiGraphics guiGraphics = event.getGuiGraphics();
            PoseStack poseStack = guiGraphics.pose();
            double weight = WeightEvent.calculateWeight(minecraft.player);
            String weightText = "Weight: " + roundDouble(weight, 3) + "/" + Math.abs(WeightEvent.getWeightWithBoostItem(minecraft.player, 1));

            // Render the weight text on the screen
            poseStack.pushPose();
            RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);

            // Get screen dimensions
            int screenWidth = minecraft.getWindow().getGuiScaledWidth();
            int screenHeight = minecraft.getWindow().getGuiScaledHeight();


            Font font = minecraft.font;

            ResourceLocation anvilIcon = ANVIL_ICON_WHITE;
            if (weight >= WeightEvent.getWeightWithBoostItem(minecraft.player, 0) && weight < WeightEvent.getWeightWithBoostItem(minecraft.player, 1)) {
                anvilIcon = ANVIL_ICON_YELLOW;
            } else if (weight >= WeightEvent.getWeightWithBoostItem(minecraft.player, 1)) {
                anvilIcon = ANVIL_ICON_RED;
            }

            // Render the anvil icon with jiggle logic
            int iconSize = 12; // Adjust the size of the icon if needed
            int icon_x = (int) (screenWidth * 0.5);
            int icon_y = screenHeight - 43;

            if (WeightEvent.getThresholdTF(ClientConfigs.TOGGLE_ANVIL_ICON) && (weight >= WeightEvent.getWeightWithBoostItem(minecraft.player,1))) {
                // Add jiggle logic
                long tickCount = minecraft.level.getGameTime();
                if (tickCount % (Math.max(1, 2) * 3 + 1) == 0) {
                    icon_y += (random.nextInt(1) - 1);
                }
            }

            int anvilOffset_x = WeightEvent.getThresholdInt(ClientConfigs.ANVIL_UI_X_OFFSET);
            int anvilOffset_y = WeightEvent.getThresholdInt(ClientConfigs.ANVIL_UI_Y_OFFSET);
            RenderSystem.setShader(GameRenderer::getPositionTexShader);
            RenderSystem.setShaderTexture(0, anvilIcon);
            RenderSystem.enableBlend();
            RenderSystem.blendFuncSeparate(770, 771, 1, 0);
            RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
            if (WeightEvent.getThresholdTF(ClientConfigs.TOGGLE_ANVIL_ICON)){
                guiGraphics.blit(anvilIcon, icon_x - (iconSize / 2) + anvilOffset_x, icon_y - (iconSize / 2) - anvilOffset_y, 0, 0, iconSize, iconSize, iconSize, iconSize);

            }
            RenderSystem.disableBlend();

            // Calculate the width of the text to center it properly
            int textWidth = font.width(weightText);

            Screen currentScreen = Minecraft.getInstance().screen;

            if (WeightEvent.getThresholdTF(ClientConfigs.TOGGLE_WEIGHT_TEXT)) {
                if (currentScreen instanceof AbstractContainerScreen && !moddedScreen(currentScreen)) {
                    if (weight < WeightEvent.getWeightWithBoostItem(minecraft.player, 0)) {
                        font.drawInBatch(weightText, icon_x - textWidth / 2, ((AbstractContainerScreen<?>) currentScreen).getGuiTop() - 12, 0xFFFFFF, true, poseStack.last().pose(), guiGraphics.bufferSource(), Font.DisplayMode.NORMAL, 0, 15728880);
                    } else if (weight >= WeightEvent.getWeightWithBoostItem(minecraft.player, 0) && weight < WeightEvent.getWeightWithBoostItem(minecraft.player, 1)) {
                        font.drawInBatch(weightText, icon_x - textWidth / 2, ((AbstractContainerScreen<?>) currentScreen).getGuiTop() - 12, 0xE9CF11, true, poseStack.last().pose(), guiGraphics.bufferSource(), Font.DisplayMode.NORMAL, 0, 15728880);
                    } else if (weight >= WeightEvent.getWeightWithBoostItem(minecraft.player, 1)) {
                        font.drawInBatch(weightText, icon_x - textWidth / 2, ((AbstractContainerScreen<?>) currentScreen).getGuiTop() - 12, 0xE30C0C, true, poseStack.last().pose(), guiGraphics.bufferSource(), Font.DisplayMode.NORMAL, 0, 15728880);
                    }
                }
            }
            poseStack.popPose();
        }
    }

    public static boolean moddedScreen(Screen screen){
        return !VanillaScreens.isVanillaScreen(screen);
    }

    public static double roundDouble(double value, int places) {
        if (places < 0) throw new IllegalArgumentException();

        BigDecimal bd = BigDecimal.valueOf(value);
        bd = bd.setScale(places, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }
}
