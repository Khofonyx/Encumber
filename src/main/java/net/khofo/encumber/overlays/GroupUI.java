package net.khofo.encumber.overlays;

import com.mojang.blaze3d.systems.RenderSystem;
import net.khofo.encumber.Encumber;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

import java.util.HashMap;
import java.util.Map;

public class GroupUI {
    private static final Map<Group, CustomEditBox> editBoxMap = new HashMap<>();
    public static int renderGroup(Group group, int x, int y, int weightX, int indent, GuiGraphics guiGraphics, double scrollAmount) {
        Font font = Minecraft.getInstance().font;

        // Draw the hitbox for debugging
        drawHitbox(guiGraphics, x, y, 200 - indent, 20, 0x55FF0000); // Red color, adjusted for indent

        // Render the group row
        guiGraphics.drawString(font, group.getName(), x + 5, y + 5, 0xFFFFFF);

        // Get or create the CustomEditBox for this group
        CustomEditBox weightField = editBoxMap.computeIfAbsent(group, g -> {
            CustomEditBox eb = new CustomEditBox(font, weightX - 3, y, 50, 19, Component.literal(""));
            eb.setValue(String.format("%.4f", g.getWeight()));
            return eb;
        });

        // Update EditBox position and render state
        weightField.setX(weightX - 3);
        weightField.setY(y);
        weightField.setEditable(!group.isExpanded());
        weightField.renderWidget(guiGraphics, 0, 0, 0);

        int currentY = y + 20; // Start the next element below this one

        // Render subgroups and base items if the group is expanded
        if (group.isExpanded()) {
            for (GroupItem subGroup : group.getSubGroups()) {
                if (subGroup instanceof BaseItem) {
                    currentY = BaseItemUI.renderBaseItem((BaseItem) subGroup, x + indent, currentY, weightX, guiGraphics, scrollAmount);
                } else if (subGroup instanceof Group) {
                    currentY = renderGroup((Group) subGroup, x + indent, currentY, weightX, indent, guiGraphics,scrollAmount);
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

    public static boolean mouseClicked(Group group, double mouseX, double mouseY, int button,double scrollAmount) {
        unfocusAll();
        CustomEditBox weightField = editBoxMap.get(group);
        if (weightField != null && weightField.isMouseOver(mouseX, mouseY + scrollAmount)) {
            System.out.println("Scroll AMOUNT: " + scrollAmount);
            if (weightField.isEditable()) {
                weightField.setFocused(true);
                weightField.onClick(mouseX, mouseY + scrollAmount);
                System.out.println("CustomEditBox mouseClicked: " + group.getName() + ", Focused: " + weightField.isFocused());
            } else {
                weightField.setFocused(false);
            }
            return true;
        } else {
            if (weightField != null) {
                weightField.setFocused(false);
            }
        }

        // Handle subgroups
        if (group.isExpanded()) {
            for (GroupItem subGroup : group.getSubGroups()) {
                if (subGroup instanceof BaseItem) {
                    if (BaseItemUI.mouseClicked((BaseItem) subGroup, mouseX, mouseY, button,scrollAmount)) {
                        return true;
                    }
                } else if (subGroup instanceof Group) {
                    if (mouseClicked((Group) subGroup, mouseX, mouseY, button,scrollAmount)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    private static void unfocusAll() {
        for (CustomEditBox editBox : editBoxMap.values()) {
            editBox.setFocused(false);
        }
        BaseItemUI.unfocusAll();
    }

    public static boolean charTyped(Group group, char chr, int modifiers) {
        CustomEditBox weightField = editBoxMap.get(group);
        if (weightField != null && weightField.isFocused() && weightField.charTyped(chr, modifiers)) {
            try {
                double newWeight = Double.parseDouble(weightField.getValue());

                // If the group is not expanded, update the group's weight and the weights of all items in its subgroups
                if (!group.isExpanded()) {
                    group.setWeight(newWeight);
                    setWeightsForSubGroups(group, newWeight);
                } else {
                    group.setWeight(newWeight);
                }

                System.out.println("CustomEditBox charTyped: " + group.getName() + ", New Weight: " + newWeight);
            } catch (NumberFormatException e) {
                // Handle invalid input if necessary
                System.out.println("Invalid number format: " + weightField.getValue());
            }
            return true;
        }

        // Handle subgroups
        if (group.isExpanded()) {
            for (GroupItem subGroup : group.getSubGroups()) {
                if (subGroup instanceof BaseItem) {
                    if (BaseItemUI.charTyped((BaseItem) subGroup, chr, modifiers)) {
                        return true;
                    }
                } else if (subGroup instanceof Group) {
                    if (charTyped((Group) subGroup, chr, modifiers)) {
                        return true;
                    }
                }
            }
        }

        return false;
    }

    private static void setWeightsForSubGroups(Group group, double newWeight) {
        for (GroupItem subGroup : group.getSubGroups()) {
            if (subGroup instanceof BaseItem) {
                ((BaseItem) subGroup).setWeight(newWeight);

                // Update the item weight in the Encumber.itemWeights map
                ResourceLocation itemName = new ResourceLocation(((BaseItem) subGroup).getName());
                Encumber.itemWeights.put(itemName, newWeight);
            } else if (subGroup instanceof Group) {
                ((Group) subGroup).setWeight(newWeight);

                // Recursively set weights for subgroups
                setWeightsForSubGroups((Group) subGroup, newWeight);
            }
        }

        // Persist the updated weights
        Encumber.updateConfigWeights();
    }

    public static void tickGroup(Group group) {
        CustomEditBox weightField = editBoxMap.get(group);
        if (weightField != null) {
            weightField.tick();
        }

        // Handle subgroups
        if (group.isExpanded()) {
            for (GroupItem subGroup : group.getSubGroups()) {
                if (subGroup instanceof BaseItem) {
                    BaseItemUI.tickBaseItem((BaseItem) subGroup);
                } else if (subGroup instanceof Group) {
                    tickGroup((Group) subGroup);
                }
            }
        }
    }


    public static boolean keyPressed(Group group, int keyCode, int scanCode, int modifiers) {
        CustomEditBox weightField = editBoxMap.get(group);
        if (weightField != null && weightField.isFocused()) {
            return weightField.keyPressed(keyCode, scanCode, modifiers);
        }

        // Handle subgroups
        if (group.isExpanded()) {
            for (GroupItem subGroup : group.getSubGroups()) {
                if (subGroup instanceof BaseItem) {
                    if (BaseItemUI.keyPressed((BaseItem) subGroup, keyCode, scanCode, modifiers)) {
                        return true;
                    }
                } else if (subGroup instanceof Group) {
                    if (keyPressed((Group) subGroup, keyCode, scanCode, modifiers)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public static boolean keyReleased(Group group, int keyCode, int scanCode, int modifiers) {
        CustomEditBox weightField = editBoxMap.get(group);
        if (weightField != null && weightField.isFocused()) {
            return weightField.keyReleased(keyCode, scanCode, modifiers);
        }

        // Handle subgroups
        if (group.isExpanded()) {
            for (GroupItem subGroup : group.getSubGroups()) {
                if (subGroup instanceof BaseItem) {
                    if (BaseItemUI.keyReleased((BaseItem) subGroup, keyCode, scanCode, modifiers)) {
                        return true;
                    }
                } else if (subGroup instanceof Group) {
                    if (keyReleased((Group) subGroup, keyCode, scanCode, modifiers)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public static boolean mouseScrolled(Group group, double mouseX, double mouseY, double delta) {
        CustomEditBox weightField = editBoxMap.get(group);
        if (weightField != null && weightField.isFocused()) {
            return weightField.mouseScrolled(mouseX, mouseY, delta);
        }

        // Handle subgroups
        if (group.isExpanded()) {
            for (GroupItem subGroup : group.getSubGroups()) {
                if (subGroup instanceof BaseItem) {
                    if (BaseItemUI.mouseScrolled((BaseItem) subGroup, mouseX, mouseY, delta)) {
                        return true;
                    }
                } else if (subGroup instanceof Group) {
                    if (mouseScrolled((Group) subGroup, mouseX, mouseY, delta)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public static boolean mouseDragged(Group group, double mouseX, double mouseY, int button, double deltaX, double deltaY) {
        CustomEditBox weightField = editBoxMap.get(group);
        if (weightField != null && weightField.isFocused()) {
            return weightField.mouseDragged(mouseX, mouseY, button, deltaX, deltaY);
        }

        // Handle subgroups
        if (group.isExpanded()) {
            for (GroupItem subGroup : group.getSubGroups()) {
                if (subGroup instanceof BaseItem) {
                    if (BaseItemUI.mouseDragged((BaseItem) subGroup, mouseX, mouseY, button, deltaX, deltaY)) {
                        return true;
                    }
                } else if (subGroup instanceof Group) {
                    if (mouseDragged((Group) subGroup, mouseX, mouseY, button, deltaX, deltaY)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }
}