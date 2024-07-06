package net.khofo.encumber.overlays;

import net.khofo.encumber.groups.ItemGroups;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.client.gui.components.Button;


public class WeightEditScreen extends Screen {
    private CustomScrollWidget customScrollWidget;

    public WeightEditScreen(Component title) {
        super(title);
    }

    @Override
    protected void init() {
        super.init();

        // Initialize root group and add test data
        Group rootGroup = new Group("Minecraft", 0.0);
        ItemGroups.initGroup(rootGroup);

        // Create a DropdownMenu with the root group
        DropdownMenu dropdownMenu = new DropdownMenu(rootGroup);

        // Initialize the CustomScrollWidget
        customScrollWidget = new CustomScrollWidget(this.width / 2 - 100, 20, 200, this.height - 60, Component.literal("Scroll Widget"));
        customScrollWidget.addDropdownMenu(dropdownMenu);

        // Add a button to the screen
        this.addRenderableWidget(Button.builder(Component.literal("Done"), (button) -> {
            this.minecraft.setScreen(null); // Close the screen
        }).bounds(this.width / 2 - 100, this.height - 30, 200, 20).build());
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
        this.renderBackground(guiGraphics);
        customScrollWidget.render(guiGraphics, mouseX, mouseY, partialTicks);
        super.render(guiGraphics, mouseX, mouseY, partialTicks);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (customScrollWidget.mouseClicked(mouseX, mouseY, button)) {
            return true;
        }
        return super.mouseClicked(mouseX, mouseY, button);
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
        if (customScrollWidget.keyPressed(keyCode, scanCode, modifiers)) {
            return true;
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean charTyped(char chr, int modifiers) {
        if (customScrollWidget.charTyped(chr, modifiers)) {
            return true;
        }
        return super.charTyped(chr, modifiers);
    }

    @Override
    public void tick() {
        super.tick();
        customScrollWidget.tick(); // Ensure customScrollWidget tick method is called
    }
}