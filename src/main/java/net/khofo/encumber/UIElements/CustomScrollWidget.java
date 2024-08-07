package net.khofo.encumber.UIElements;

import net.khofo.encumber.Encumber;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractScrollWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.ArrayList;
import java.util.List;

import static net.khofo.encumber.UIElements.BaseItemUI.isMouseOverItemName;


@OnlyIn(Dist.CLIENT)
public class CustomScrollWidget extends AbstractScrollWidget {
    /**
     * scrollAmount: variable to keep track of how many pixels you've scrolled down
     * scrolling: whether or not you are scrolling.
     * dropdownMenu's: a list of dropdowns present in the scroll widget
     */

    private boolean searchActive = false;
    private double scrollAmount;
    private boolean scrolling;
    private final List<DropdownMenu> dropdownMenus;

    private final List<BaseItem> baseItems;

    /**
     * Default constructor to create a scroll widget
     */
    public CustomScrollWidget(int pX, int pY, int pWidth, int pHeight, Component pMessage) {
        super(pX, pY, pWidth, pHeight, pMessage);
        dropdownMenus = new ArrayList<>();
        baseItems = new ArrayList<>();
    }

    /**
     * method to add a dropdown menu to the dropdownMenu's list
     */
    public void addDropdownMenu(DropdownMenu dropdownMenu) {
        dropdownMenus.add(dropdownMenu);
    }


    public void addBaseItem(BaseItem baseItem) {
        baseItems.add(baseItem);
    }

    public void clear() {
        dropdownMenus.clear();
        baseItems.clear();
    }

    public void setSearchActive(boolean active) {
        searchActive = active;
    }


    /**
     * method to
     */
    @Override
    protected int getInnerHeight() {
        int height = 0;
        for (DropdownMenu menu : dropdownMenus) {
            height += menu.calculateHeight();
        }
        /* =======================================================BLACK MAGIC =====================================================
         I DON'T KNOW WHY I HAVE TO ADD THIS.HEIGHT AND DIVIDE BY 2 BUT IT WORKS */
        height += baseItems.size() * 22;
        return (height + this.height  + 22)/2;
    }


    @Override
    protected double scrollRate() {
        return 20.0;
    }

    @Override
    protected void renderContents(GuiGraphics pGuiGraphics, int pMouseX, int pMouseY, float pPartialTick) {
        int y = this.getY() - (int) this.scrollAmount();
        if (searchActive) {
            for (BaseItem item : baseItems) {
                y = BaseItemUI.renderBaseItem(item, this.getX() + 18, y, this.getX() + 182, pGuiGraphics, pMouseX, pMouseY, this.scrollAmount);
            }
        } else {
            boolean isFirstMenu = true;
            for (DropdownMenu menu : dropdownMenus) {
                if (isFirstMenu) {
                    y = menu.render(this.getX() + 18, y + 1, this.getX() + 182, 20, pGuiGraphics, scrollAmount, pMouseX, pMouseY);
                    isFirstMenu = false;
                } else {
                    y = menu.render(this.getX() + 18, y + 2, this.getX() + 182, 20, pGuiGraphics, scrollAmount, pMouseX, pMouseY);
                }
            }
        }
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (button == 0) {
            int scrollbarXStart = this.getX() + this.getWidth()-20;
            int scrollbarXEnd = scrollbarXStart + 8;
            int scrollbarYStart = this.getY() + (int) (this.scrollAmount * (this.height - this.getScrollBarHeight()) / this.getMaxScrollAmount());
            int scrollbarYEnd = scrollbarYStart + this.getScrollBarHeight();

            if (mouseX >= scrollbarXStart && mouseX <= scrollbarXEnd && mouseY >= scrollbarYStart && mouseY <= scrollbarYEnd) {
                this.scrolling = true;
                return true;
            }
        }

        int y = this.getY() - (int) this.scrollAmount(); // Adjust starting Y position based on scroll amount
        int indent = 0;

        boolean isFirstGroup = true;
        for (DropdownMenu menu : dropdownMenus) {
            Group group = menu.getGroup();
            if (GroupUI.mouseClicked(group, mouseX, mouseY, button, this.scrollAmount())) {
                return true;
            }

            // Adjust mouseY based on whether it is the first group or not
            if (isFirstGroup) {
                if (menu.mouseClicked(mouseX, mouseY, this.getX() + 18, y, indent, this.scrollAmount())) {
                    return true;
                }
                isFirstGroup = false;
            } else {
                if (menu.mouseClicked(mouseX, mouseY, this.getX() + 18, y + 1, indent, this.scrollAmount())) {
                    return true;
                }
            }

            y += menu.calculateHeight() + (isFirstGroup ? 1 : 2); // Adjust the y position incrementally
        }

        for (BaseItem item : baseItems) {
            if (BaseItemUI.mouseClicked(item, mouseX, mouseY, button, this.scrollAmount())) {
                return true;
            }
            y += 22; // Assuming each BaseItem row height is 22
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
        boolean flag = pKeyCode == 265; // Up arrow
        boolean flag1 = pKeyCode == 264; // Down arrow
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

        for (BaseItem item : baseItems) {
            if (BaseItemUI.keyPressed(item, pKeyCode, pScanCode, pModifiers)) {
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
        }
    }


    public void renderOnlyDecorations(GuiGraphics pGuiGraphics, int pMouseX, int pMouseY) {
        if (this.visible) {
            this.renderDecorations(pGuiGraphics, pMouseX, pMouseY);
        }
    }


    private int getScrollBarHeight() {
        return Mth.clamp((int)((float)(this.height * this.height) / (float)this.getInnerHeight()), 32, this.height);
    }

    protected void renderDecorations(GuiGraphics pGuiGraphics,int pMouseX, int pMouseY) {
        ResourceLocation gui_frame_top = new ResourceLocation(Encumber.MOD_ID, "textures/gui/weight_gui_frame_top.png");
        ResourceLocation gui_frame_extend = new ResourceLocation(Encumber.MOD_ID, "textures/gui/weight_gui_frame_extendable.png");
        ResourceLocation gui_frame_bottom = new ResourceLocation(Encumber.MOD_ID, "textures/gui/weight_gui_frame_bottom.png");
        if (this.scrollbarVisible()) {
            this.renderScrollBar1(pGuiGraphics);
        }
        // Render Tooltip here instead of from my BaseItemUI's class since BaseItem is rendered inside renderContents() which happens while scissor is enabled
        int yOffset = this.getY(); // Initialize yOffset with the starting Y coordinate of the widget
        for (BaseItem item : baseItems) {
            if (isMouseOverItemName(this.getX() + 20, yOffset - (int)this.scrollAmount() * 2 + 5, Minecraft.getInstance().font, item, pMouseX, pMouseY, 0)) {
                double screenMouseX = Minecraft.getInstance().mouseHandler.xpos() * (Minecraft.getInstance().getWindow().getGuiScaledWidth() / (double)Minecraft.getInstance().getWindow().getScreenWidth());
                double screenMouseY = Minecraft.getInstance().mouseHandler.ypos() * (Minecraft.getInstance().getWindow().getGuiScaledHeight() / (double)Minecraft.getInstance().getWindow().getScreenHeight());
                pGuiGraphics.renderTooltip(Minecraft.getInstance().font, item.getItemStack(), (int)screenMouseX,(int) screenMouseY);
            }
            yOffset += 22; // Adjust the offset for the next item
        }
        pGuiGraphics.blit(gui_frame_top, this.getX(), this.getY()-41, 0, 0, 256,41,256,41);
        pGuiGraphics.blit(gui_frame_extend, this.getX(), this.getY(), 0, 0, 256,this.height,256,this.height);
        pGuiGraphics.blit(gui_frame_bottom, this.getX(), this.getY()+this.height-3, 0, 0, 256,39,256,39);
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

    protected void renderBackground(GuiGraphics pGuiGraphics) {
        this.renderBorder(pGuiGraphics, this.getX(), this.getY(), this.getWidth(), this.getHeight());
    }

    protected void renderBorder(GuiGraphics pGuiGraphics, int pX, int pY, int pWidth, int pHeight) {
        pGuiGraphics.fill(pX, pY-27, pX + pWidth - 1, pY + pHeight+10, -16777216);
    }

    private void renderScrollBar1(GuiGraphics pGuiGraphics) {
        int scrollBarHeight = this.getScrollBarHeight();
        int scrollBarXStart = this.getX() + this.width - 20;
        int scrollBarYStart = Math.max(this.getY(), (int) this.scrollAmount * (this.height - scrollBarHeight) / this.getMaxScrollAmount() + this.getY());

        ResourceLocation scroll_bar_top = new ResourceLocation("encumber", "textures/gui/scroll_bar_top.png");
        ResourceLocation scroll_bar_middle = new ResourceLocation("encumber", "textures/gui/scroll_bar_middle.png");
        ResourceLocation scroll_bar_bottom = new ResourceLocation("encumber", "textures/gui/scroll_bar_bottom.png");

        int topHeight = 2; // Assuming the top part is 2 pixels high
        int bottomHeight = 2; // Assuming the bottom part is 2 pixels high

        // Render top part of the scrollbar
        pGuiGraphics.blit(scroll_bar_top, scrollBarXStart, scrollBarYStart-5, 0, 0, 8, topHeight,8,topHeight);

        // Render middle part of the scrollbar, resized to fill the space between top and bottom
        int middleHeight = scrollBarHeight - topHeight - bottomHeight;
        if (middleHeight > 0) {
            pGuiGraphics.blit(scroll_bar_middle, scrollBarXStart, scrollBarYStart + topHeight -5, 0, 0, 8, middleHeight,8,middleHeight);
        }
        // Render bottom part of the scrollbar
        pGuiGraphics.blit(scroll_bar_bottom, scrollBarXStart, scrollBarYStart + scrollBarHeight - bottomHeight - 5, 0, 0, 8, bottomHeight,8,bottomHeight);
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
        for (BaseItem item : baseItems) {
            if (BaseItemUI.charTyped(item, chr, modifiers)) {
                return true;
            }
        }
        return super.charTyped(chr, modifiers);
    }

    public void tick() {
        for (DropdownMenu menu : dropdownMenus) {
            Group group = menu.getGroup();
            GroupUI.tickGroup(group);
        }
        for (BaseItem item : baseItems) {
            BaseItemUI.tickBaseItem(item);
        }
    }
}
