package net.khofo.encumber.UIElements;

import com.mojang.blaze3d.systems.RenderSystem;
import net.khofo.encumber.Encumber;
import net.khofo.encumber.groups.ItemGroups;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.Renderable;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.client.gui.components.Button;
import net.minecraft.resources.ResourceLocation;
import org.lwjgl.glfw.GLFW;
import java.util.*;

public class WeightEditScreen extends Screen {
    private String previousSearchQuery = "";
    private CustomScrollWidget customScrollWidget;
    private EditBox searchBox;
    private List<BaseItem> allItems;
    private List<BaseItem> filteredItems = new ArrayList<>();
    List<Group> modGroups;
    private double previousEBoxValue = 0.0;
    CustomEditBox eBox;
    CustomButton exitButton;
    CustomButton anvil_badge;
    boolean confirm_weight;
    public WeightEditScreen(Component title) {
        super(title);
    }

    @Override
    protected void init() {
        super.init();

        // Initialize the groups for each mod
        modGroups = ItemGroups.initGroups();


        // Initialize the CustomScrollWidget
        customScrollWidget = new CustomScrollWidget(this.width / 2 - 128, 60, 256, this.height - 120, Component.literal("Scroll Widget"));


        // Create a DropdownMenu for each mod group and add it to the CustomScrollWidget
        for (Group modGroup : modGroups) {
            DropdownMenu dropdownMenu = new DropdownMenu(modGroup);
            customScrollWidget.addDropdownMenu(dropdownMenu);
        }

        // Initialize the search box
        searchBox = new EditBox(this.font, this.width / 2 - 107, customScrollWidget.getY() -29, 145, 16, Component.literal("Search"));
        searchBox.setMaxLength(1028);
        searchBox.setResponder(this::onSearchChanged);
        this.addRenderableWidget(searchBox);
        searchBox.setValue(previousSearchQuery);

        // Initialize all items from itemWeights
        allItems = new ArrayList<>();
        for (Map.Entry<ResourceLocation, Double> entry : Encumber.itemWeights.entrySet()) {
            allItems.add(new BaseItem(entry.getKey().toString(), entry.getValue()));
        }

        eBox = new CustomEditBox(font, this.width / 2 +52, customScrollWidget.getY() -28, 41, 17, Component.literal(""));
        eBox.setEditable(true);
        eBox.setValue(""+previousEBoxValue);
        eBox.setResponder(this::onEBoxChanged);

        onSearchChanged(previousSearchQuery);

        exitButton = new CustomButton(this.width / 2 - 38,this.height - 52,76,18,Component.literal("Exit"),(button) -> {this.minecraft.setScreen(null);},"textures/gui/exit_button.png");
        this.addRenderableWidget(exitButton);

        anvil_badge = new CustomButton(this.width / 2 +102,customScrollWidget.getY() -28,14,14,Component.literal(""),(button) -> {
            confirm_weight = true;
            onEBoxChanged(eBox.getValue());
            },"textures/gui/anvil_badge.png","Click To Confirm");
        this.addRenderableWidget(anvil_badge);
        anvil_badge.visible = false;
    }

    private void onEBoxChanged(String newValue) {
        try {
            if(searchBox.getValue().equals("")){
                anvil_badge.visible = false;
            }else{
                anvil_badge.visible = true;
            }
            if(confirm_weight){
                double newWeight = Double.parseDouble(newValue);
                for (BaseItem item : filteredItems) {
                    item.setWeight(newWeight);
                    ResourceLocation itemName = new ResourceLocation(item.getName());
                    Encumber.itemWeights.put(itemName, newWeight);

                    CustomEditBox weightField = BaseItemUI.editBoxMap.get(item);
                    if (weightField != null) {
                        weightField.setValue(newValue);  // Update the value in the edit box
                    }
                }
                Encumber.updateConfigWeights();
                previousEBoxValue = newWeight;
                confirm_weight = false;
                anvil_badge.visible = false;
            }
        } catch (NumberFormatException e) {
            System.out.println("Invalid number format: " + newValue);
        }
    }

    private void onSearchChanged(String searchQuery) {
        if (!searchQuery.equals(previousSearchQuery)) {
            filteredItems.clear();
            customScrollWidget.clear();

            if (searchQuery.isEmpty()) {
                customScrollWidget.setSearchActive(false);
                for (Group modGroup : modGroups) {
                    DropdownMenu dropdownMenu = new DropdownMenu(modGroup);
                    customScrollWidget.addDropdownMenu(dropdownMenu);
                }
            } else {
                customScrollWidget.setSearchActive(true);
                Set<BaseItem> includedItems = new HashSet<>();

                String[] parts = searchQuery.split("\\|");

                for (String part : parts) {
                    part = part.trim();
                    boolean isModSpecific = part.startsWith("@");
                    String query = isModSpecific ? part.substring(1).trim() : part;

                    // Split the part into inclusion and exclusion terms
                    List<String> includeTerms = new ArrayList<>();
                    List<String> excludeTerms = new ArrayList<>();

                    for (String subQuery : query.split(" ")) {
                        if (subQuery.startsWith("-")) {
                            excludeTerms.add(subQuery.substring(1).toLowerCase());
                        } else {
                            includeTerms.add(subQuery.toLowerCase());
                        }
                    }

                    for (BaseItem item : allItems) {
                        String itemName = item.getName().toLowerCase();
                        String modName = getModNameFromRegistryName(itemName);

                        boolean matchesInclude = includeTerms.isEmpty() || includeTerms.stream().allMatch(itemName::contains);
                        boolean matchesExclude = excludeTerms.stream().anyMatch(itemName::contains);

                        if (isModSpecific) {
                            String modQuery = includeTerms.isEmpty() ? "" : includeTerms.get(0); // First term is the mod name
                            String itemQuery = includeTerms.size() > 1 ? String.join(" ", includeTerms.subList(1, includeTerms.size())) : "";

                            if (modName.contains(modQuery) && itemName.contains(itemQuery) && !matchesExclude) {
                                includedItems.add(item);
                            }
                        } else {
                            if (matchesInclude && !matchesExclude) {
                                includedItems.add(item);
                            }
                        }
                    }
                }

                filteredItems.addAll(includedItems);
                for (BaseItem item : includedItems) {
                    customScrollWidget.addBaseItem(item);
                }
            }

            previousSearchQuery = searchQuery;  // Update the previous search query
        }
    }


    private String getModNameFromRegistryName(String registryName) {
        // Assuming the registry name is in the format "modid:itemname"
        int colonIndex = registryName.indexOf(':');
        if (colonIndex > 0) {
            return registryName.substring(0, colonIndex);
        }
        return "";
    }
    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
        this.renderBackground(guiGraphics);
        searchBox.render(guiGraphics, mouseX, mouseY, partialTicks);
        customScrollWidget.renderWidget(guiGraphics, mouseX, mouseY, partialTicks);
        eBox.renderWidget(guiGraphics, mouseX, mouseY, partialTicks);
        customScrollWidget.renderOnlyDecorations(guiGraphics, mouseX, mouseY);
        exitButton.renderWidget(guiGraphics, mouseX, mouseY, partialTicks);
        if(searchBox.getValue().equals("")){
            anvil_badge.visible = false;
        }
        for (Renderable widget : this.renderables) {
            if (widget != searchBox && widget != customScrollWidget && widget != eBox && widget != exitButton) {
                widget.render(guiGraphics, mouseX, mouseY, partialTicks);
            }
        }
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        boolean searchBoxClicked = searchBox.mouseClicked(mouseX, mouseY, button);
        boolean eBoxClicked = eBox.mouseClicked(mouseX, mouseY, button);
        boolean customScrollWidgetClicked = customScrollWidget.mouseClicked(mouseX, mouseY, button);

        if (searchBoxClicked) {
            searchBox.setFocused(true);
        } else {
            searchBox.setFocused(false);
        }

        if (eBoxClicked) {
            eBox.setFocused(true);
        } else {
            eBox.setFocused(false);
        }

        if (customScrollWidgetClicked) {
            return true;
        }

        return searchBoxClicked || eBoxClicked || super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double delta) {
        if (customScrollWidget.mouseScrolled(mouseX, mouseY, delta)) {
            return true;
        }
        return super.mouseScrolled(mouseX, mouseY, delta);
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double dragX, double dragY) {
        if (customScrollWidget.mouseDragged(mouseX, mouseY, button, dragX, dragY)) {
            return true;
        }
        return super.mouseDragged(mouseX, mouseY, button, dragX, dragY);
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        if (customScrollWidget.mouseReleased(mouseX, mouseY, button)) {
            return true;
        }
        return super.mouseReleased(mouseX, mouseY, button);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (searchBox.isFocused()) {
            if (keyCode == GLFW.GLFW_KEY_TAB) {
                searchBox.setFocused(false);
                return true;
            }
            if (searchBox.keyPressed(keyCode, scanCode, modifiers)) {
                searchBox.setFocused(true);
                return true;
            }
        } else if(eBox.isFocused()) {
            if(eBox.keyPressed(keyCode,scanCode,modifiers)){
                eBox.setFocused(true);
                return true;
            }
        }else {
            for (BaseItem item : filteredItems) {
                CustomEditBox weightField = BaseItemUI.editBoxMap.get(item);
                if (weightField != null && weightField.isFocused() && weightField.keyPressed(keyCode, scanCode, modifiers)) {
                    return true;
                }
            }
            if (customScrollWidget.keyPressed(keyCode, scanCode, modifiers)) {
                return true;
            }
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean charTyped(char chr, int modifiers) {
        if (searchBox.charTyped(chr, modifiers)) {
            return true;
        }
        if (customScrollWidget.charTyped(chr, modifiers)) {
            return true;
        }
        if(eBox.charTyped(chr,modifiers)){
            return true;
        }
        return super.charTyped(chr, modifiers);
    }


    @Override
    public void tick() {
        super.tick();
        searchBox.tick();
        customScrollWidget.tick(); // Ensure customScrollWidget tick method is called
        eBox.tick();
    }
}