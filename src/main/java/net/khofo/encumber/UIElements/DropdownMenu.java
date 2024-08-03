package net.khofo.encumber.UIElements;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class DropdownMenu {
    /**
     * group: the group you are inserting into the dropdown menu.
     */
    private final Group group;

    /**
     * Default constructor to create a new DropdownMenu
     */
    public DropdownMenu(Group group) {
        this.group = group;
    }

    /**
     * Method to return the DropdownMenu's group
     */
    public Group getGroup() {
        return group;
    }

    /**
     * Method that renders the dropdown menu.
     * This calls GroupUI's render method which handles displaying either a BaseItem or another nested group.
     */
    public int render(int x, int y, int weightX, int indent, GuiGraphics guiGraphics, double scrollAmount,int mouseX,int mouseY) {
        // Call GroupUI's render method which handles rendering the UI for the elements inside of the group
        return GroupUI.renderGroup(group, x, y, weightX, indent, guiGraphics, scrollAmount,mouseX,mouseY);
    }

    /**
     * Method that happens once your mouse is clicked.
     * In here, we are checking if the click was on the dropdown menu's box or not.
     * Depending on the current state of the dropdown menu, we either expand it or collapse it.
     */
    public boolean mouseClicked(double mouseX, double mouseY, int x, int y, int indent, double scrollAmount) {
        // The height of each row whether that be a BaseItem or another group is by default 20
        int rowHeight = 20;

        // This is the x dimension of the dropdown menu's clickable region.
        // We start at 160 by default, and every indent we subtract the indent about.
        // So if we indented 40 pixels, we shrink the clickable region by 40 pixels
        int maxClickableWidth = 160 - indent;

        // This is the check for whether or not the clicked position was inside of the dropdown's box.
        // if it was, we use the groups toggleExpanded method which inverts the state of the group to either expanded or collapsed.
        if ((mouseX >= x) && (mouseX <= x + maxClickableWidth) && (mouseY >= y - scrollAmount) && (mouseY <= y - scrollAmount + rowHeight)) {
            group.toggleExpanded();
            return true;
        }

        int currentY = y + rowHeight;

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

    /**
     * method to calculate the height of a dropdown menu.
     * By default, the height of each item in the dropdown is 20 pixels tall.
     * If the group is closed, we know the height is only 20 as it's just showing the main dropdown box.
     * If the group is expanded, we iterate through each child of the group and add 20 for each element since
     * every row is 20 pixels tall.
     */
    public int calculateHeight() {
        // Again, height is default 20 pixels.
        int height = 20;

        // If our group is expanded, we iterate through each child element adding 20 for every element.
        if (group.isExpanded()) {
            for (GroupItem child : group.getSubGroups()) {
                if (child instanceof Group) {
                    // If the child is another group, call this method on itself and recursively add 20 for each sub element.
                    int childHeight = new DropdownMenu((Group) child).calculateHeight();
                    height += childHeight;
                } else {
                    // If the child is not a group, implying that it is a BaseItem, we just add 20 since a base item row is just 20 pixels tall.
                    height += 20;
                }
            }
        }
        return height;
    }

    /**
     * ????????????????????????????????????????????
     */
    public void tick() {
        GroupUI.tickGroup(group);
    }
}