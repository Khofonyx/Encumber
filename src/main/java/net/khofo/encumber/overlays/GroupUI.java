package net.khofo.encumber.overlays;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;

public class GroupUI {
    public static int renderGroup(Group group, int x, int y, int weightX, int indent, GuiGraphics guiGraphics) {
        Font font = Minecraft.getInstance().font;

        // Render the group row
        guiGraphics.drawString(font, group.getName(), x, y, 0xFFFFFF); // Draw group name
        guiGraphics.drawString(font, String.format("%.4f", group.getWeight()), weightX, y, 0xFFFFFF); // Draw group weight

        int currentY = y + 25; // Start the next element below this one

        // If the group has subgroups, render them as well
        for (GroupItem subGroup : group.getSubGroups()) {
            if (subGroup instanceof BaseItem) {
                currentY = BaseItemUI.renderBaseItem((BaseItem) subGroup, x + indent, currentY, weightX, guiGraphics);
            } else if (subGroup instanceof Group) {
                currentY = renderGroup((Group) subGroup, x + indent, currentY, weightX, indent, guiGraphics);
            }
        }

        return currentY;
    }
}