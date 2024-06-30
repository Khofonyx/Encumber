package net.khofo.encumber.overlays;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

public class WeightEditScreen extends Screen {
    private static final ResourceLocation GUI_TEXTURE = new ResourceLocation("encumber", "textures/gui/inventory.png");

    public WeightEditScreen(Component title) {
        super(title);
    }

    @Override
    protected void init() {
        super.init();
        // Initialize buttons and fields here
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
        renderBackground(guiGraphics);
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderTexture(0, GUI_TEXTURE);
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);

        // Draw the entire 128x128 texture centered on the screen
        int x = (this.width - 128) / 2;
        int y = (this.height - 128) / 2;
        guiGraphics.blit(GUI_TEXTURE, x, y, 0, 0, 128, 128, 128, 128);

        RenderSystem.disableBlend();
        super.render(guiGraphics, mouseX, mouseY, partialTicks);
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}