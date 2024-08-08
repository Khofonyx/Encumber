package net.khofo.encumber.UIElements;

import net.khofo.encumber.Encumber;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import org.lwjgl.glfw.GLFW;

import java.util.HashMap;
import java.util.Map;

public class GroupUI {
    private static final Map<Group, CustomEditBox> editBoxMap = new HashMap<>();
    public static int renderGroup(Group group, int x, int y, int weightX, int indent, GuiGraphics guiGraphics, double scrollAmount,int mouseX,int mouseY) {
        Font font = Minecraft.getInstance().font;

        CustomEditBox weightField = editBoxMap.computeIfAbsent(group, g -> {
            CustomEditBox eb = new CustomEditBox(font, weightX, y, 37, 16, Component.literal(""));
            eb.setValue(""+g.getWeight());
            return eb;
        });

        weightField.setX(weightX - 2);
        weightField.setY(y+4);
        weightField.setEditable(!group.isExpanded());
        weightField.renderWidget(guiGraphics, 0, 0, 0);

        drawHitbox(guiGraphics, x, y, 204, 22,group);

        guiGraphics.drawString(font, group.getName(), x + 5, y +6, 0xFFFFFF);

        int currentY = y + 22;

        if (group.isExpanded()) {
            for (GroupItem subGroup : group.getSubGroups()) {
                if (subGroup instanceof BaseItem) {
                    currentY = BaseItemUI.renderBaseItem((BaseItem) subGroup, x + indent, currentY, weightX, guiGraphics,mouseX,mouseY,scrollAmount);
                } else if (subGroup instanceof Group) {
                    currentY = renderGroup((Group) subGroup, x + indent, currentY, weightX, indent, guiGraphics, scrollAmount,mouseX,mouseY);
                }
            }
        }
        return currentY;
    }

    private static void drawHitbox(GuiGraphics guiGraphics, int x, int y, int width, int height,Group group) {
        if(group.isExpanded()){
            ResourceLocation group_box = new ResourceLocation(Encumber.MOD_ID, "textures/gui/weight_gui_group_and_edit_box_pressed.png");
            guiGraphics.blit(group_box, x, y, 0, 0, width,height,width,height);
        }else{
            ResourceLocation group_box = new ResourceLocation(Encumber.MOD_ID, "textures/gui/weight_gui_group_and_edit_box.png");
            guiGraphics.blit(group_box, x, y, 0, 0, width,height,width,height);
        }
    }

    public static boolean mouseClicked(Group group, double mouseX, double mouseY, int button,double scrollAmount) {
        unfocusAll();
        CustomEditBox weightField = editBoxMap.get(group);
        if (weightField != null && weightField.isMouseOver(mouseX, mouseY + scrollAmount)) {
            if (weightField.isEditable()) {
                weightField.setFocused(true);
                weightField.onClick(mouseX, mouseY + scrollAmount);
            } else {
                weightField.setFocused(false);
            }
            return true;
        } else {
            if (weightField != null) {
                weightField.setFocused(false);
            }
        }

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

                if (!group.isExpanded()) {
                    group.setWeight(newWeight);
                    setWeightsForSubGroups(group, newWeight);
                } else {
                    group.setWeight(newWeight);
                }
            } catch (NumberFormatException e) {
                System.out.println("Invalid number format: " + weightField.getValue());
            }
            return true;
        }

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

                ResourceLocation itemName = new ResourceLocation(subGroup.getName());
                Encumber.itemWeights.put(itemName, newWeight);
            } else if (subGroup instanceof Group) {
                ((Group) subGroup).setWeight(newWeight);

                setWeightsForSubGroups((Group) subGroup, newWeight);
            }
        }

        Encumber.updateConfigWeights();
    }

    public static void tickGroup(Group group) {
        CustomEditBox weightField = editBoxMap.get(group);
        if (weightField != null) {
            weightField.tick();
        }

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
        if (weightField != null){
            if ((weightField.isFocused() && keyCode == GLFW.GLFW_KEY_LEFT) || (weightField.isFocused() && keyCode == GLFW.GLFW_KEY_RIGHT) || (weightField.isFocused() && keyCode == GLFW.GLFW_KEY_BACKSPACE)) {
                return weightField.keyPressed(keyCode, scanCode, modifiers);
            }
        }

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
        }else{

        }
        return false;
    }
}