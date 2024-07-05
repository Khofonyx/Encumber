package net.khofo.encumber.overlays;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;

public class BaseItemUI {
    public static int renderBaseItem(BaseItem item, int x, int y, int weightX, GuiGraphics guiGraphics) {
        Font font = Minecraft.getInstance().font;

        // Render the base item row
        guiGraphics.drawString(font, item.getName(), x, y, 0xFFFFFF);
        guiGraphics.drawString(font, String.format("%.4f", item.getWeight()), weightX, y, 0xFFFFFF);

        // Return the new y position for the next element
        return y + 20;
    }
}