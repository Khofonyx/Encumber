package net.khofo.encumber.overlays;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;

import java.util.HashMap;
import java.util.Map;

public class BaseItemUI {
    private static final Map<BaseItem, CustomEditBox> editBoxMap = new HashMap<>();
    public static int renderBaseItem(BaseItem item, int x, int y, int weightX, GuiGraphics guiGraphics, double scrollAmount) {
        Font font = Minecraft.getInstance().font;

        // Render the base item row
        guiGraphics.drawString(font, item.getName(), x, y, 0xFFFFFF);


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