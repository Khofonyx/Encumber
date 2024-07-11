package net.khofo.encumber.overlays;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractScrollWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;

import java.util.ArrayList;
import java.util.List;


public class CustomScrollWidget extends AbstractScrollWidget {
    private double scrollAmount;
    private boolean scrolling;

    private final List<DropdownMenu> dropdownMenus;

    public CustomScrollWidget(int pX, int pY, int pWidth, int pHeight, Component pMessage) {
        super(pX, pY, pWidth, pHeight, pMessage);
        dropdownMenus = new ArrayList<>();
    }

    public void addDropdownMenu(DropdownMenu dropdownMenu) {
        dropdownMenus.add(dropdownMenu);
    }

    @Override
    protected int getInnerHeight() {
        int height = 0;
        for (DropdownMenu menu : dropdownMenus) {
            height += menu.calculateHeight();
        }
        System.out.println("Calculated inner height: " + height);
        return height;
    }


    @Override
    protected double scrollRate() {
        return 20.0;
    }

    @Override
    protected void renderContents(GuiGraphics pGuiGraphics, int pMouseX, int pMouseY, float pPartialTick) {
        int startY = this.getY();
        int endY = this.getY() + this.height;
        int y = startY - (int) this.scrollAmount(); // Adjust starting Y position based on scroll amount
        int indent = 20;

        System.out.println("Starting render at Y: " + y + " with scroll amount: " + this.scrollAmount() + " startY: " + startY + " endY: " + endY);

        for (DropdownMenu menu : dropdownMenus) {
            int menuHeight = menu.calculateHeight();
            System.out.println("Menu height: " + menuHeight);

            // Check if the menu is within the visible area
            if (y + menuHeight > startY && y < endY) {
                System.out.println("Rendering menu at Y: " + y);
                y = menu.render(this.getX(), y, this.getX() + 165, indent, pGuiGraphics, scrollAmount);
            } else {
                // Skip rendering if the menu is outside the visible area
                y += menuHeight;
                System.out.println("Skipping menu at Y: " + y + " with height: " + menuHeight);
            }
        }
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (button == 0) {
            int scrollbarXStart = this.getX() + this.getWidth();
            int scrollbarXEnd = scrollbarXStart + 6;
            int scrollbarYStart = this.getY() + (int) (this.scrollAmount * (this.height - this.getScrollBarHeight()) / this.getMaxScrollAmount());
            int scrollbarYEnd = scrollbarYStart + this.getScrollBarHeight();

            if (mouseX >= scrollbarXStart && mouseX <= scrollbarXEnd && mouseY >= scrollbarYStart && mouseY <= scrollbarYEnd) {
                this.scrolling = true;
                return true;
            }
        }

        int y = this.getY() - (int) this.scrollAmount(); // Adjust starting Y position based on scroll amount
        int indent = 0;

        for (DropdownMenu menu : dropdownMenus) {
            Group group = menu.getGroup();
            if (GroupUI.mouseClicked(group, mouseX, mouseY, button, this.scrollAmount())) {
                return true;
            }

            if (menu.mouseClicked(mouseX, mouseY, this.getX(), y, indent, this.scrollAmount())) {
                return true;
            }
            y += menu.calculateHeight();
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseReleased(double pMouseX, double pMouseY, int pButton) {
        if (pButton == 0) {
            this.scrolling = false;
        }

        return super.mouseReleased(pMouseX, pMouseY, pButton);
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double dragX, double dragY) {
        if (this.visible && this.scrolling) {
            if (mouseY < (double) this.getY()) {
                this.setScrollAmount(0.0D);
            } else if (mouseY > (double) (this.getY() + this.height)) {
                this.setScrollAmount(this.getMaxScrollAmount());
            } else {
                int scrollbarHeight = this.getScrollBarHeight();
                double scrollRatio = (double) Math.max(1, this.getMaxScrollAmount() / (this.height - scrollbarHeight));
                this.setScrollAmount(this.scrollAmount() + dragY * scrollRatio);
            }
            return true;
        } else {
            return super.mouseDragged(mouseX, mouseY, button, dragX, dragY);
        }
    }

    public boolean mouseScrolled(double pMouseX, double pMouseY, double pDelta) {
        if (!this.visible) {
            return false;
        } else {
            this.setScrollAmount(this.scrollAmount() - (pDelta * this.scrollRate()));
            return true;
        }
    }

    @Override
    public boolean keyPressed(int pKeyCode, int pScanCode, int pModifiers) {
        boolean flag = pKeyCode == 265;
        boolean flag1 = pKeyCode == 264;
        if (flag || flag1) {
            double d0 = this.scrollAmount;
            this.setScrollAmount(this.scrollAmount + (double) (flag ? -1 : 1) * this.scrollRate());
            if (d0 != this.scrollAmount) {
                return true;
            }
        }

        for (DropdownMenu menu : dropdownMenus) {
            Group group = menu.getGroup();
            if (GroupUI.keyPressed(group, pKeyCode, pScanCode, pModifiers)) {
                return true;
            }
        }

        return super.keyPressed(pKeyCode, pScanCode, pModifiers);
    }

    public void renderWidget(GuiGraphics pGuiGraphics, int pMouseX, int pMouseY, float pPartialTick) {
        if (this.visible) {
            this.renderBackground(pGuiGraphics);
            pGuiGraphics.enableScissor(this.getX() + 1, this.getY() + 1, this.getX() + this.width - 1, this.getY() + this.height - 1);
            pGuiGraphics.pose().pushPose();
            pGuiGraphics.pose().translate(0.0D, -this.scrollAmount, 0.0D);
            this.renderContents(pGuiGraphics, pMouseX, pMouseY, pPartialTick);
            pGuiGraphics.pose().popPose();
            pGuiGraphics.disableScissor();
            this.renderDecorations(pGuiGraphics);
        }
    }

    /*private int getScrollBarHeight() {
        int innerHeight = this.getInnerHeight();
        if (innerHeight == 0) {
            return 0;
        } else {
            return Math.max(32, this.height * this.height / innerHeight);
        }
    }*/

    private int getScrollBarHeight() {
        return Mth.clamp((int)((float)(this.height * this.height) / (float)this.getInnerHeight()), 32, this.height);
    }

    protected void renderDecorations(GuiGraphics pGuiGraphics) {
        if (this.scrollbarVisible()) {
            this.renderScrollBar1(pGuiGraphics);
        }

    }

    protected double scrollAmount() {
        return this.scrollAmount;
    }

    //private double lastScrollAmount = -1;

    @Override
    public void setScrollAmount(double scrollAmount) {
        double maxScroll = Math.max(0, getInnerHeight() - this.height);
        this.scrollAmount = Math.max(0, Math.min(scrollAmount, maxScroll));
        System.out.println("Set scroll amount: " + this.scrollAmount + " (Max: " + maxScroll + ")");
    }

    protected int getMaxScrollAmount() {
        return this.getInnerHeight() - this.height;
    }

    protected void renderBackground(GuiGraphics pGuiGraphics) {
        this.renderBorder(pGuiGraphics, this.getX(), this.getY(), this.getWidth(), this.getHeight());
    }

    protected void renderBorder(GuiGraphics pGuiGraphics, int pX, int pY, int pWidth, int pHeight) {
        int i = this.isFocused() ? -1 : -6250336;
        pGuiGraphics.fill(pX, pY, pX + pWidth, pY + pHeight, i);
        pGuiGraphics.fill(pX + 1, pY + 1, pX + pWidth - 1, pY + pHeight - 1, -16777216);
    }

    private void renderScrollBar1(GuiGraphics pGuiGraphics) {
        if (this.scrollbarVisible()) {
            int scrollbarXStart = this.getX() + this.getWidth();
            int scrollbarXEnd = scrollbarXStart + 6;
            int scrollbarYStart = this.getY() + (int) (this.scrollAmount * (this.height - this.getScrollBarHeight()) / this.getMaxScrollAmount());
            int scrollbarYEnd = scrollbarYStart + this.getScrollBarHeight();
            //System.out.println("Rendering scrollbar from Y: " + scrollbarYStart + " to Y: " + scrollbarYEnd);
            pGuiGraphics.fillGradient(scrollbarXStart, scrollbarYStart, scrollbarXEnd, scrollbarYEnd, 0xFFAAAAAA, 0xFF888888);
        }
    }
    protected boolean scrollbarVisible() {
        return this.height < this.getInnerHeight();
    }

    @Override
    protected void updateWidgetNarration(NarrationElementOutput pNarrationElementOutput) {
        // Provide a narration for the widget if needed
    }

    @Override
    public boolean charTyped(char chr, int modifiers) {
        for (DropdownMenu menu : dropdownMenus) {
            Group group = menu.getGroup();
            if (GroupUI.charTyped(group, chr, modifiers)) {
                return true;
            }
        }
        return super.charTyped(chr, modifiers);
    }

    public void tick() {
        for (DropdownMenu menu : dropdownMenus) {
            menu.tick();
        }
    }
}
