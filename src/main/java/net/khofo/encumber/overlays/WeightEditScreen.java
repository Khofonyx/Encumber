package net.khofo.encumber.overlays;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.client.gui.components.Button;

public class WeightEditScreen extends Screen {
    private static final ResourceLocation GUI_TEXTURE = new ResourceLocation("encumber", "textures/gui/inventory_background.png");

    public WeightEditScreen(Component title) {
        super(title);
    }

    @Override
    protected void init() {
        super.init();

        // Add a button that exits the screen
        int x = (this.width - 226) / 2;
        int y = (this.height - 226) / 2;
        this.addRenderableWidget(new Button.Builder(Component.literal("Exit"), button -> {
            this.onClose();
        })
        .pos(x +93, y+200)
        .size(40, 16)
        .build());

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
        int x = (this.width - 226) / 2;
        int y = (this.height - 226) / 2;
        guiGraphics.blit(GUI_TEXTURE, x, y, 0, 0, 226, 226, 226, 226);

        RenderSystem.disableBlend();
        super.render(guiGraphics, mouseX, mouseY, partialTicks);
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}