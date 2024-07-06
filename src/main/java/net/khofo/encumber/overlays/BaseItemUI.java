package net.khofo.encumber.overlays;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;


import java.util.HashMap;
import java.util.Map;

public class BaseItemUI {
    private static final Map<BaseItem, CustomEditBox> editBoxMap = new HashMap<>();
    public static int renderBaseItem(BaseItem item, int x, int y, int weightX, GuiGraphics guiGraphics, double scrollAmount) {
        Font font = Minecraft.getInstance().font;
        ItemRenderer itemRenderer = Minecraft.getInstance().getItemRenderer();

        // Render the base item row
        guiGraphics.drawString(font, item.getName(), x + 20, y, 0xFFFFFF);

        // Render the item icon
        guiGraphics.renderItem(item.getItemStack(),x,y);

        // Get or create the CustomEditBox for this base item
        CustomEditBox weightField = editBoxMap.computeIfAbsent(item, i -> {
            CustomEditBox eb = new CustomEditBox(font, weightX - 3, y, 50, 19, Component.literal(""));
            eb.setValue(String.format("%.4f", i.getWeight()));
            return eb;
        });

        // Update EditBox position and render state
        weightField.setX(weightX - 3);
        weightField.setY(y);
        weightField.setEditable(true); // BaseItems are always editable
        weightField.renderWidget(guiGraphics, 0, 0, 0);

        // Return the new y position for the next element
        return y + 20;
    }

    private static int getCombinedLight(int blockLight, int skyLight) {
        return (skyLight << 20) | (blockLight << 4);
    }


    public static boolean mouseClicked(BaseItem item, double mouseX, double mouseY, int button, double scrollAmount) {
        unfocusAll();
        CustomEditBox weightField = editBoxMap.get(item);
        if (weightField != null && weightField.isMouseOver(mouseX, mouseY + scrollAmount)) {
            if (weightField.isEditable()) {
                weightField.setFocused(true);
                weightField.onClick(mouseX, mouseY + scrollAmount);
                System.out.println("CustomEditBox mouseClicked: " + item.getName() + ", Focused: " + weightField.isFocused());
            } else {
                weightField.setFocused(false);
            }
            return true;
        } else {
            if (weightField != null) {
                weightField.setFocused(false);
            }
        }
        return false;
    }

    public static boolean charTyped(BaseItem item, char chr, int modifiers) {
        CustomEditBox weightField = editBoxMap.get(item);
        if (weightField != null && weightField.isFocused() && weightField.charTyped(chr, modifiers)) {
            try {
                double newWeight = Double.parseDouble(weightField.getValue());
                item.setWeight(newWeight);
                System.out.println("CustomEditBox charTyped: " + item.getName() + ", New Weight: " + newWeight);
            } catch (NumberFormatException e) {
                // Handle invalid input if necessary
                System.out.println("Invalid number format: " + weightField.getValue());
            }
            return true;
        }
        return false;
    }

    public static void unfocusAll() {
        for (CustomEditBox editBox : editBoxMap.values()) {
            editBox.setFocused(false);
        }
    }

    public static void tickBaseItem(BaseItem item) {
        CustomEditBox weightField = editBoxMap.get(item);
        if (weightField != null) {
            weightField.tick();
        }
    }

    public static boolean keyPressed(BaseItem item, int keyCode, int scanCode, int modifiers) {
        CustomEditBox weightField = editBoxMap.get(item);
        if (weightField != null && weightField.isFocused()) {
            return weightField.keyPressed(keyCode, scanCode, modifiers);
        }
        return false;
    }

    public static boolean keyReleased(BaseItem item, int keyCode, int scanCode, int modifiers) {
        CustomEditBox weightField = editBoxMap.get(item);
        if (weightField != null && weightField.isFocused()) {
            return weightField.keyReleased(keyCode, scanCode, modifiers);
        }
        return false;
    }

    public static boolean mouseScrolled(BaseItem item, double mouseX, double mouseY, double delta) {
        CustomEditBox weightField = editBoxMap.get(item);
        if (weightField != null && weightField.isFocused()) {
            return weightField.mouseScrolled(mouseX, mouseY, delta);
        }
        return false;
    }

    public static boolean mouseDragged(BaseItem item, double mouseX, double mouseY, int button, double deltaX, double deltaY) {
        CustomEditBox weightField = editBoxMap.get(item);
        if (weightField != null && weightField.isFocused()) {
            return weightField.mouseDragged(mouseX, mouseY, button, deltaX, deltaY);
        }
        return false;
    }
}