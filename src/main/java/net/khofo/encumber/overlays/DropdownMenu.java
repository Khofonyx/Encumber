package net.khofo.encumber.overlays;

import net.minecraft.client.gui.GuiGraphics;

public class DropdownMenu {

    private final Group group;

    public DropdownMenu(Group group) {
        this.group = group;
    }

    public Group getGroup() {
        return group;
    }

    public int render(int x, int y, int weightX, int indent, GuiGraphics guiGraphics, double scrollAmount) {
        return GroupUI.renderGroup(group, x, y, weightX, indent, guiGraphics, scrollAmount);
    }

    public boolean mouseClicked(double mouseX, double mouseY, int x, int y, int indent, double scrollAmount) {
        int titleHeight = 20;
        int maxClickableWidth = 160 - indent;

        if ((mouseX >= x) && (mouseX <= x + maxClickableWidth) && (mouseY >= y - scrollAmount) && (mouseY <= y - scrollAmount + titleHeight)) {
            group.toggleExpanded();
            return true;
        }

        int currentY = y + titleHeight;

        if (group.isExpanded()) {
            for (GroupItem child : group.getSubGroups()) {
                if (child instanceof Group) {
                    DropdownMenu childMenu = new DropdownMenu((Group) child);
                    if (childMenu.mouseClicked(mouseX, mouseY, x + 20, currentY, indent + 20, scrollAmount)) {
                        return true;
                    }
                    currentY += childMenu.calculateHeight();
                } else {
                    currentY += 20;
                }
            }
        }

        return false;
    }
    public int calculateHeight() {
        int height = 20;
        if (group.isExpanded()) {
            for (GroupItem child : group.getSubGroups()) {
                if (child instanceof Group) {
                    DropdownMenu childMenu = new DropdownMenu((Group) child);
                    height += childMenu.calculateHeight();
                } else {
                    height += 20;
                }
            }
        }
        return height;
    }

    public void tick() {
        GroupUI.tickGroup(group);
    }
}