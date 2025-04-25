package net.refractionapi.refraction.feature.screen.tab;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextColor;
import net.minecraft.util.Mth;
import net.refractionapi.refraction.feature.screen.RefractionScreen;
import org.jetbrains.annotations.ApiStatus;

import java.awt.*;
import java.util.Arrays;
import java.util.Optional;

/**
 * This system will be a lot more fleshed out later <br>
 * With draggable / resizable / re-arrangeable tabs later on. <br>
 * Marked as {@link ApiStatus.Internal} for now <br>
 * This is just the basics for a system I want :P --Zeus
 */
@ApiStatus.Internal
public abstract class TabbedScreen extends Screen implements RefractionScreen {
    private final TabNavigator navigator = new TabNavigator();
    private Tab[] tabs = new Tab[0];
    private Tab currentTab;
    private Tab navigatorTab;
    protected int xSize;
    protected int ySize;
    private int borderSize;

    protected TabbedScreen(Component name) {
        super(name);
    }

    @Override
    protected void init() {
        this.tabs = new Tab[0];
        this.currentTab = null;
        this.navigatorTab = null;
        initSettings();
        Tab navigatorTab = createTab(Component.literal("Navigator"), 97, this.ySize); // TODO make TabNavigator useful; this is just a placeholder
        navigatorTab.hideName();
        this.navigatorTab = navigatorTab; // TODO remove and create activeTabs array
        initTabs();
        for (int i = 1; i < this.tabs.length; i++) {
            Tab tab = this.tabs[i];
            int finalI = i;
            navigatorTab.addButton(tab.name, 97, 20, button -> switchTab(finalI));
        }
        this.changeTab(null, this.navigatorTab);
    }

    protected abstract void initSettings();

    protected void setSize(int xSize, int ySize) {
        this.xSize = xSize;
        this.ySize = ySize;
    }

    protected void setBorderSize(int borderSize) {
        this.borderSize = borderSize;
    }

    protected int[] getPosition() {
        return new int[]{(this.width - this.xSize) / 2, (this.height - this.ySize) / 2};
    }

    protected int[] getTabPosition(Tab tab) {
        int previousTab = getIndex(tab) - 1;
        return new int[]{(this.width + (previousTab == -1 ? -this.xSize : -tab.xSize + (this.tabs[0].xSize))) / 2, (this.height - tab.ySize) / 2}; // TODO terrible, but without the final system, this will do
    }

    protected abstract Color getBackgroundColor();

    protected abstract Color getBorderColor();

    protected abstract void initTabs();

    public Tab createTab(Component name) {
        return createTab(name, this.xSize - this.navigatorTab.xSize, this.ySize);
    }

    private Tab createTab(Component name, int xSize, int ySize) {
        Tab tab = new Tab(name, xSize, ySize, this);
        Tab[] newTabs = new Tab[this.tabs.length + 1];
        System.arraycopy(this.tabs, 0, newTabs, 0, this.tabs.length);
        newTabs[this.tabs.length] = tab;
        this.tabs = newTabs;
        return tab;
    }

    public void switchTab(int index) {
        changeTab(this.currentTab, this.currentTab = this.tabs[index]);
    }

    private void changeTab(Tab oldTab, Tab newTab) {
        if (oldTab != null) {
            for (TabWidget widget : oldTab.getWidgets()) {
                this.removeWidget(widget.widget);
            }
        }
        if (newTab != null) {
            for (TabWidget widget : newTab.getWidgets()) {
                this.addWidget(widget.widget);
            }
        }
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
        this.renderBackground(guiGraphics, mouseX, mouseY, partialTicks);
        if (this.navigatorTab != null)
            renderTab(this.navigatorTab, guiGraphics, mouseX, mouseY, partialTicks);
        if (this.currentTab != null)
            renderTab(this.currentTab, guiGraphics, mouseX, mouseY, partialTicks);
    }

    public void renderTab(Tab tab, GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
        int[] position = getTabPosition(tab);
        int padding = 3;
        int minX = position[0];
        int minY = position[1];
        int xSize = tab.xSize;
        int ySize = tab.ySize;
        int maxX = minX + xSize;
        int maxY = minY + ySize;

        renderBackground(guiGraphics, minX, minY, maxX, maxY);

        if (!tab.hideName) {
            int textX = maxX - this.borderSize - padding;
            TextColor textColor = tab.name.getStyle().getColor();
            String text = tab.name.getString();
            Component sequence = Component.literal("%s%s".formatted(text, " ".repeat(text.length() + this.borderSize + this.borderSize / 3))).withStyle(tab.name.getStyle());
            guiGraphics.drawCenteredString(this.minecraft.font, sequence, textX, minY + 2, textColor == null ? 16777215 : textColor.getValue());
        }

        int yOffset = -tab.yScroll;
        guiGraphics.enableScissor(minX, minY, maxX, maxY);
        for (TabWidget<?> tabWidget : tab.getWidgets()) {
            int paddingLeft = tabWidget.getLeftPadding();
            int paddingRight = tabWidget.getRightPadding();
            int paddingBottom = tabWidget.getBottomPadding();
            int paddingTop = tabWidget.getTopPadding();
            tabWidget.widget.setPosition(minX + paddingLeft - paddingRight, minY + yOffset + paddingTop - paddingBottom);
            tabWidget.widget.render(guiGraphics, mouseX, mouseY, partialTicks);
            if (tabWidget.nameShown) {
                AbstractWidget widget = tabWidget.widget;
                guiGraphics.drawString(this.font, widget.getMessage(), minX + paddingLeft - paddingRight + widget.getWidth() + 5, minY + yOffset + paddingTop - paddingBottom + widget.getHeight() / 2 + 2, 14737632 | 255 << 24);
            }
            yOffset += tabWidget.widget.getHeight();
        }
        guiGraphics.disableScissor();
    }

    protected Tab getTab(double mouseX, double mouseY) {
        Optional<Tab> ret = Arrays.stream(this.tabs).filter((tab) -> {
            int[] tabPos = getTabPosition(tab);
            int minX = tabPos[0];
            int minY = tabPos[1];
            int maxX = minX + tab.xSize;
            int maxY = minY + tab.ySize;
            return (this.currentTab == tab || this.navigatorTab == tab) && mouseX <= maxX && mouseX >= minX && mouseY <= maxY && mouseY >= minY; // TODO convert to activeTabs array
        }).findFirst();
        return ret.orElse(null);
    }

    public void renderBackground(GuiGraphics guiGraphics, int minX, int minY, int maxX, int maxY) {
        Color backgroundColor = getBackgroundColor();
        Color borderColor = getBorderColor();
        guiGraphics.fill(minX - this.borderSize, minY - this.borderSize, maxX + this.borderSize, maxY + this.borderSize, borderColor.getRGB());
        guiGraphics.fill(minX, minY, maxX, maxY, backgroundColor.getRGB());
    }

    @Override
    public boolean mouseScrolled(double pMouseX, double pMouseY, double pScrollX, double pScrollY) {
        Tab tab = getTab(pMouseX, pMouseY);
        if (tab == null || !tab.isScrollable) return false;
        int maxScroll = tab.getTotalY() - tab.ySize;
        tab.yScroll = Mth.clamp(tab.yScroll - (int) pScrollY * 5, 0, maxScroll / 2);
        return super.mouseScrolled(pMouseX, pMouseY, pScrollX, pScrollY);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int release) {
        return getTab(mouseX, mouseY) != null && super.mouseClicked(mouseX, mouseY, release);
    }

    public Tab currentTab() {
        return this.currentTab;
    }

    public TabNavigator navigator() {
        return this.navigator;
    }

    private int getIndex(Tab tab) { // TODO remove
        for (int i = 0; i < this.tabs.length; i++) {
            if (this.tabs[i] == tab) {
                return i;
            }
        }
        return -1;
    }

    @Override
    public void handleServerEvent(Code code, CompoundTag tag) {

    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}
