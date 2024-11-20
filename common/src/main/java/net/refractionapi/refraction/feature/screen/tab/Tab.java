package net.refractionapi.refraction.feature.screen.tab;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.network.chat.Component;
import net.refractionapi.refraction.client.screen.widget.RCheckbox;

import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class Tab {

    private TabWidget[] widgets = new TabWidget[0];
    protected final Component name;
    protected boolean isScrollable = false;
    private final Minecraft minecraft = Minecraft.getInstance();
    private final TabbedScreen screen;
    protected int xSize;
    protected int ySize;
    protected boolean hideName = false;
    protected int yScroll = 0;

    protected Tab(Component name, int xSize, int ySize, TabbedScreen screen) {
        this.name = name;
        this.screen = screen;
        this.xSize = xSize;
        this.ySize = ySize;
    }

    public TabWidget<RCheckbox> addCheckbox(Component descriptor, int size, boolean selected, Consumer<RCheckbox> consumer) {
        RCheckbox checkbox = new RCheckbox(0, 0, size, size, descriptor, selected, consumer);
        return (TabWidget<RCheckbox>) this.createWidget(checkbox);
    }

    public TabWidget<RCheckbox> addCheckbox(Component descriptor, int size, boolean selected) {
        return this.addCheckbox(descriptor, size, selected, (c -> {
        }));
    }

    public TabWidget<EditBox> addTextInput(Component descriptor, int width, int height, BiConsumer<EditBox, String> onChange) {
        EditBox box = new EditBox(this.minecraft.font, 0, 0, width, height, descriptor);
        box.setResponder((string) -> onChange.accept(box, string));
        return (TabWidget<EditBox>) this.createWidget(box);
    }

    public TabWidget<EditBox> addNumberInput(Component descriptor, int width, int height) {
        return this.addTextInput(descriptor, width, height, (box, string) -> {
            if (!string.isEmpty() && !string.matches("[0-9]+")) {
                box.setValue(string.replaceAll("[^0-9]", ""));
            }
        });
    }

    public TabWidget<EditBox> addBlockPosInput(Component descriptor, int width, int height) {
        return this.addTextInput(descriptor, width, height, (box, string) -> { // TODO Sanitize input
        });
    }

    public TabWidget<EditBox> addBlockInput(Component descriptor, int width, int height) {
        return this.addTextInput(descriptor, width, height, (box, string) -> { // TODO Sanitize input
        });
    }

    public TabWidget<Button> addButton(Component descriptor, int width, int height, Button.OnPress onPress) {
        Button button = Button.builder(descriptor, onPress).size(width, height).build();
        TabWidget<?> widget = this.createWidget(button);
        widget.hideName();
        return (TabWidget<Button>) widget;
    }

    protected TabWidget<?> createWidget(AbstractWidget abstractWidget) {
        TabWidget<?> widget = new TabWidget<>(abstractWidget, this);
        this.addWidget(widget);
        return widget;
    }

    protected void addWidget(TabWidget<?> widget) {
        TabWidget<?>[] newTabs = new TabWidget[this.widgets.length + 1];
        System.arraycopy(this.widgets, 0, newTabs, 0, this.widgets.length);
        newTabs[this.widgets.length] = widget;
        this.widgets = newTabs;
        this.isScrollable = this.getTotalY() > this.screen.ySize;
    }

    public void hideName() {
        this.hideName = true;
    }

    protected int getTotalY() {
        int totalY = 0;
        for (TabWidget<?> widget : this.widgets) {
            totalY += widget.widget.getHeight() + widget.getTopPadding() - widget.getBottomPadding();
        }
        return totalY;
    }

    public TabWidget<?>[] getWidgets() {
        return this.widgets;
    }

}
