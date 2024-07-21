package net.khofo.encumber.UIElements;

import net.khofo.encumber.Encumber;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;


import java.util.HashMap;
import java.util.Map;

public class BaseItemUI {
    /**
     * editBoxMap: a map of item's to edit boxes. Creates a link to the physical Box UI element to the BaseItem
     * font: vanilla minecraft font
     */
    private static final Map<BaseItem, CustomEditBox> editBoxMap = new HashMap<>();
    private static final Font font = Minecraft.getInstance().font;

    /**
     * method that renders the BaseItem's UI elements.
     * Renders the items picture, name, and edit box where you enter the weight.
     */
    public static int renderBaseItem(BaseItem item, int x, int y, int weightX, GuiGraphics guiGraphics) {
        // This renders the item name
        renderItemName(guiGraphics,font,item,x,y+5);

        // This renders the item's image from the item stack
        guiGraphics.renderItem(item.getItemStack(),x,y);

        // This creates a custom edit box for the item
        CustomEditBox weightField = editBoxMap.computeIfAbsent(item, i -> {
            CustomEditBox eb = new CustomEditBox(font, weightX - 3, y, 50, 19, Component.literal(""));
            eb.setValue(String.format("%.4f", i.getWeight()));
            return eb;
        });

        // These set the position of the edit box
        weightField.setX(weightX - 3);
        weightField.setY(y);

        // This allows the box to be editable initially
        weightField.setEditable(true);

        // This renders the edit box to the screen
        weightField.renderWidget(guiGraphics, 0, 0, 0);

        return y + 20;
    }

    /**
     * Helper method to render the item name
     */
    public static void renderItemName(GuiGraphics guiGraphics, Font font, BaseItem item, int x, int y) {
        // Grabs the registry name of the item. Ex: minecraft:stone
        String fullItemName = item.getName();
        // Splits the full name to everything after the ':' and saves it to itemName. Ex: minecraft:stone -> stone
        String itemName = fullItemName.contains(":") ? fullItemName.split(":")[1] : fullItemName;
        // Renders that item name to the screen
        guiGraphics.drawString(font, itemName, x + 20, y, 0xFFFFFF);
    }

    /**
     * method that gets executed when a mouse click event occurs.
     */
    public static boolean mouseClicked(BaseItem item, double mouseX, double mouseY, int button, double scrollAmount) {
        // Unhighlight all boxes when the mouse is clicked in a new spot
        unfocusAll();
        // get the edit box map for the base item clicked
        CustomEditBox weightField = editBoxMap.get(item);
        if (weightField != null && weightField.isMouseOver(mouseX, mouseY + scrollAmount)) {
            if (weightField.isEditable()) {
                // if the mouse is over the edit box, and it is editable, set as the focused box.
                weightField.setFocused(true);
                weightField.onClick(mouseX, mouseY + scrollAmount);
            } else {
                // if not editable, set the focus to false.
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

    /**
     * Method that detects a character input from player
     */
    public static boolean charTyped(BaseItem item, char chr, int modifiers) {
        CustomEditBox weightField = editBoxMap.get(item);
        // If we've clicked on an edit box of a baseItem, and a character was typed, determine the new weight entered
        if (weightField != null && weightField.isFocused() && weightField.charTyped(chr, modifiers)) {
            try {
                // Get the value entered in the box
                double newWeight = Double.parseDouble(weightField.getValue());
                // Set the weight of the item to that entered value
                item.setWeight(newWeight);
                // Get the item name of the
                ResourceLocation itemName = new ResourceLocation(item.getName());
                // Update the itemWeights hashmap to the new weight entered.
                Encumber.itemWeights.put(itemName, newWeight);
                // Update the config file's weights
                Encumber.updateConfigWeights();
            } catch (NumberFormatException e) {
                System.out.println("Invalid number format: " + weightField.getValue());
            }
            return true;
        }
        return false;
    }

    /**
     * Method to unhighlight set the focus of all the edit boxes
     */
    public static void unfocusAll() {
        for (CustomEditBox editBox : editBoxMap.values()) {
            editBox.setFocused(false);
        }
    }

    /**
     * ????????????????????????????????????????????
     */
    public static void tickBaseItem(BaseItem item) {
        CustomEditBox weightField = editBoxMap.get(item);
        if (weightField != null) {
            weightField.tick();
        }
    }

    /**
     * Method that executes every time a key is pressed.
     */
    public static boolean keyPressed(BaseItem item, int keyCode, int scanCode, int modifiers) {
        // Grab the edit box
        CustomEditBox weightField = editBoxMap.get(item);
        if (weightField != null && weightField.isFocused()) {
            // If the edit box is clicked on, and a key is pressed, call editbox's key pressed method
            return weightField.keyPressed(keyCode, scanCode, modifiers);
        }
        return false;
    }
}