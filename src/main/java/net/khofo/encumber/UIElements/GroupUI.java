package net.khofo.encumber.UIElements;

import com.mojang.blaze3d.systems.RenderSystem;
import net.khofo.encumber.Encumber;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.HashMap;
import java.util.Map;

@OnlyIn(Dist.CLIENT)
public class GroupUI {
    private static final Map<Group, CustomEditBox> editBoxMap = new HashMap<>();
    public static int renderGroup(Group group, int x, int y, int weightX, int indent, GuiGraphics guiGraphics, double scrollAmount,int mouseX,int mouseY) {
        Font font = Minecraft.getInstance().font;

        drawHitbox(guiGraphics, x, y, 200 - indent - 2, 18, 0x55808080);
        drawHitbox(guiGraphics, x, y, 200 - indent, 20, 0x55AAAAAA);

        guiGraphics.drawString(font, group.getName(), x + 5, y + 5, 0xFFFFFF);

        CustomEditBox weightField = editBoxMap.computeIfAbsent(group, g -> {
            CustomEditBox eb = new CustomEditBox(font, weightX - 3, y, 50, 19, Component.literal(""));
            eb.setValue(String.format("%.4f", g.getWeight()));
            return eb;
        });

        weightField.setX(weightX - 3);
        weightField.setY(y);
        weightField.setEditable(!group.isExpanded());
        weightField.renderWidget(guiGraphics, 0, 0, 0);

        int currentY = y + 20;

        if (group.isExpanded()) {
            for (GroupItem subGroup : group.getSubGroups()) {
                if (subGroup instanceof BaseItem) {
                    currentY = BaseItemUI.renderBaseItem((BaseItem) subGroup, x + indent, currentY, weightX, guiGraphics,mouseX,mouseY,scrollAmount);
                } else if (subGroup instanceof Group) {
                    int previousY = currentY;
                    currentY = renderGroup((Group) subGroup, x + indent, currentY, weightX, indent, guiGraphics, scrollAmount,mouseX,mouseY);
                }
            }
        }
        return currentY;
    }

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
        if (weightField != null && weightField.isFocused()) {
            return weightField.keyPressed(keyCode, scanCode, modifiers);
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
        }
        return false;
    }
}