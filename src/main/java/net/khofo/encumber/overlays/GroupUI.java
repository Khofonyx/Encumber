package net.khofo.encumber.overlays;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.GameRenderer;

public class GroupUI {
    public static int renderGroup(Group group, int x, int y, int weightX, int indent, GuiGraphics guiGraphics) {
        Font font = Minecraft.getInstance().font;

        // Draw the hitbox for debugging
        drawHitbox(guiGraphics, x, y, 200 - indent, 20, 0x55FF0000); // Red color, adjusted for indent

        // Render the group row
        guiGraphics.drawString(font, group.getName(), x, y, 0xFFFFFF);
        guiGraphics.drawString(font, String.format("%.4f", group.getWeight()), weightX, y, 0xFFFFFF);

        int currentY = y + 20; // Start the next element below this one

        // Render subgroups and base items if the group is expanded
        if (group.isExpanded()) {
            for (GroupItem subGroup : group.getSubGroups()) {
                if (subGroup instanceof BaseItem) {
                    currentY = BaseItemUI.renderBaseItem((BaseItem) subGroup, x + indent, currentY, weightX, guiGraphics);
                } else if (subGroup instanceof Group) {
                    currentY = renderGroup((Group) subGroup, x + indent, currentY, weightX, indent, guiGraphics);
                }
                // No need to add extra spacing here as the renderBaseItem and renderGroup methods already handle spacing
            }
        }
        return currentY;
    }

    // Helper method to draw the hitbox for debugging
    private static void drawHitbox(GuiGraphics guiGraphics, int x, int y, int width, int height, int color) {
        RenderSystem.setShader(GameRenderer::getPositionColorShader);
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        guiGraphics.fillGradient(x, y, x + width, y + height, color, color);
        RenderSystem.disableBlend();
    }
}