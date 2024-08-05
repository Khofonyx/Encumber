package net.khofo.encumber.UIElements;

import net.khofo.encumber.Encumber;
import net.khofo.encumber.groups.ItemGroups;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.client.gui.components.Button;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.lwjgl.glfw.GLFW;

import java.util.*;

public class WeightEditScreen extends Screen {
    private CustomScrollWidget customScrollWidget;

    private EditBox searchBox;

    private List<BaseItem> allItems;
    private List<BaseItem> filteredItems = new ArrayList<>();

    List<Group> modGroups;

    public WeightEditScreen(Component title) {
        super(title);
    }

    @Override
    protected void init() {
        super.init();

        // Initialize the groups for each mod
        modGroups = ItemGroups.initGroups();


        // Initialize the CustomScrollWidget
        customScrollWidget = new CustomScrollWidget(this.width / 2 - 100, 20, 200, this.height - 60, Component.literal("Scroll Widget"));


        // Create a DropdownMenu for each mod group and add it to the CustomScrollWidget
        for (Group modGroup : modGroups) {
            DropdownMenu dropdownMenu = new DropdownMenu(modGroup);
            customScrollWidget.addDropdownMenu(dropdownMenu);
        }

        // Initialize the search box
        searchBox = new EditBox(this.font, this.width / 2 - 99, 2, 198, 16, Component.literal("Search"));
        searchBox.setMaxLength(1028);
        searchBox.setResponder(this::onSearchChanged);
        this.addRenderableWidget(searchBox);

        // Initialize all items from itemWeights
        allItems = new ArrayList<>();
        for (Map.Entry<ResourceLocation, Double> entry : Encumber.itemWeights.entrySet()) {
            allItems.add(new BaseItem(entry.getKey().toString(), entry.getValue()));
        }


        // Add a button to the screen
        this.addRenderableWidget(Button.builder(Component.literal("Done"), (button) -> {
            this.minecraft.setScreen(null); // Close the screen
        }).bounds(this.width / 2 - 100, this.height - 30, 200, 20).build());
    }

    private void onSearchChanged(String searchQuery) {
        filteredItems.clear();
        customScrollWidget.clear();

        if (searchQuery.isEmpty()) {
            for (Group modGroup : modGroups) {
                DropdownMenu dropdownMenu = new DropdownMenu(modGroup);
                customScrollWidget.addDropdownMenu(dropdownMenu);
            }
        } else {
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
        customScrollWidget.render(guiGraphics, mouseX, mouseY, partialTicks);
        searchBox.render(guiGraphics, mouseX, mouseY, partialTicks);
        super.render(guiGraphics, mouseX, mouseY, partialTicks);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        boolean searchBoxClicked = searchBox.mouseClicked(mouseX, mouseY, button);
        if (searchBoxClicked) {
            searchBox.setFocused(true);
        } else {
            searchBox.setFocused(false);
        }

        if (customScrollWidget.mouseClicked(mouseX, mouseY, button)) {
            return true;
        }

        return searchBoxClicked || super.mouseClicked(mouseX, mouseY, button);
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
                //customScrollWidget.focusNext();
                return true;
            }
            if (searchBox.keyPressed(keyCode, scanCode, modifiers)) {
                return true;
            }
        } else {
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
        return super.charTyped(chr, modifiers);
    }


    @Override
    public void tick() {
        super.tick();
        searchBox.tick();
        customScrollWidget.tick(); // Ensure customScrollWidget tick method is called
    }
}