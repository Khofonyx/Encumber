package net.khofo.encumber.UIElements;


import com.mojang.blaze3d.systems.RenderSystem;
import net.khofo.encumber.Encumber;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractButton;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;
import java.awt.*;
import java.util.List;
import java.util.Optional;

public class CustomButton extends AbstractButton {
    ResourceLocation button_png;
    protected final CustomButton.OnPress onPress;
    private Component tooltip_text;
    @Override
    protected void renderWidget(GuiGraphics pGuiGraphics, int pMouseX, int pMouseY, float pPartialTick) {
        RenderSystem.enableBlend();
        RenderSystem.enableDepthTest();
        pGuiGraphics.blit(button_png,this.getX(), this.getY(), 0, 0, width,height,width,height);
        int i = getFGColor();
        this.renderString(pGuiGraphics, Minecraft.getInstance().font, i | Mth.ceil(this.alpha * 255.0F) << 24);
        if (this.isHovered()) {
            if(tooltip_text != null){
                this.renderTooltip(pGuiGraphics,this.tooltip_text, pMouseX, pMouseY);
            }
        }
    }
    protected CustomButton(int pX, int pY, int pWidth, int pHeight, Component pMessage, CustomButton.OnPress pOnPress,String png_path,String tooltipText) {
        super(pX, pY, pWidth, pHeight, pMessage);
        this.onPress = pOnPress;
        this.button_png = new ResourceLocation(Encumber.MOD_ID, png_path);
        this.tooltip_text = Component.literal(tooltipText);
    }

    protected CustomButton(int pX, int pY, int pWidth, int pHeight, Component pMessage, CustomButton.OnPress pOnPress,String png_path) {
        super(pX, pY, pWidth, pHeight, pMessage);
        this.onPress = pOnPress;
        this.button_png = new ResourceLocation(Encumber.MOD_ID, png_path);
    }

    public void onPress() {
        this.onPress.onPress(this);
    }

    public void updateWidgetNarration(NarrationElementOutput pNarrationElementOutput) {
        this.defaultButtonNarrationText(pNarrationElementOutput);
    }

    @OnlyIn(Dist.CLIENT)
    public interface OnPress {
        void onPress(CustomButton pButton);
    }

    private void renderTooltip(GuiGraphics pGuiGraphics, Component comp, int pMouseX, int pMouseY) {
        if (this.tooltip_text != null) {
            pGuiGraphics.renderTooltip(Minecraft.getInstance().font,comp ,pMouseX,pMouseY);
        }
    }
}
