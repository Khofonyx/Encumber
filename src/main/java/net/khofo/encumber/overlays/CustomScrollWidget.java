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
        return height;
    }

    @Override
    protected double scrollRate() {
        return 20.0;
    }

    @Override
    protected void renderContents(GuiGraphics pGuiGraphics, int pMouseX, int pMouseY, float pPartialTick) {
        int y = this.getY() - (int) this.scrollAmount(); // Adjust starting Y position based on scroll amount
        int indent = 20;

        for (DropdownMenu menu : dropdownMenus) {
            y = menu.render(this.getX(), y, this.getX() + 165, indent, pGuiGraphics, scrollAmount);
        }
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
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
        if (this.visible && this.isFocused() && this.scrolling) {
            if (mouseY < (double)this.getY()) {
                this.setScrollAmount(0.0D);
            } else if (mouseY > (double)(this.getY() + this.height)) {
                this.setScrollAmount(this.getMaxScrollAmount());
            } else {
                int scrollbarHeight = this.getScrollBarHeight();
                double scrollRatio = (double)Math.max(1, this.getMaxScrollAmount() / (this.height - scrollbarHeight));
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
            //System.out.println("Original Scroll Amount: " + this.scrollAmount());
            //System.out.println("pDelta: " + pDelta);
            this.setScrollAmount(this.scrollAmount() - (pDelta * this.scrollRate()));
            //System.out.println("New Scroll Amount: " + this.scrollAmount());
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

    private int getScrollBarHeight() {
        int innerHeight = this.getInnerHeight();
        if (innerHeight == 0) {
            return 0;
        } else {
            return Math.max(32, this.height * this.height / innerHeight);
        }
    }

    protected void renderDecorations(GuiGraphics pGuiGraphics) {
        if (this.scrollbarVisible()) {
            this.renderScrollBar1(pGuiGraphics);
        }

    }

    protected int innerPadding() {
        return 4;
    }

    protected int totalInnerPadding() {
        return this.innerPadding() * 2;
    }

    protected double scrollAmount() {
        return this.scrollAmount;
    }

    @Override
    public void setScrollAmount(double scrollAmount) {
        double maxScroll = Math.max(0, getInnerHeight() - this.height);
        this.scrollAmount = Math.max(0, Math.min(scrollAmount, maxScroll));
    }

    protected int getMaxScrollAmount() {
        return this.getInnerHeight() - this.height;
    }

    private int getContentHeight() {
        return this.getInnerHeight() + 4;
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
            int scrollbarYStart = this.getY() + (int)(this.scrollAmount * (this.height - this.getScrollBarHeight()) / this.getMaxScrollAmount());
            int scrollbarYEnd = scrollbarYStart + this.getScrollBarHeight();
            pGuiGraphics.fillGradient(scrollbarXStart, scrollbarYStart, scrollbarXEnd, scrollbarYEnd, 0xFFAAAAAA, 0xFF888888);
        }
    }

    protected boolean withinContentAreaTopBottom(int pTop, int pBottom) {
        return (double)pBottom - this.scrollAmount >= (double)this.getY() && (double)pTop - this.scrollAmount <= (double)(this.getY() + this.height);
    }

    protected boolean withinContentAreaPoint(double pX, double pY) {
        return pX >= (double)this.getX() && pX < (double)(this.getX() + this.width) && pY >= (double)this.getY() && pY < (double)(this.getY() + this.height);
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
