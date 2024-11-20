package net.refractionapi.refraction.feature.screen.tab;

import net.minecraft.client.gui.components.AbstractWidget;

public class TabWidget<T extends AbstractWidget> {

    protected final T widget;
    private final Tab tab;
    private final int[] padding = new int[]{0, 0, 0, 0}; // left, right, bottom, top
    protected boolean nameShown = true;

    public TabWidget(T widget, Tab tab) {
        this.widget = widget;
        this.tab = tab;
    }

    public void setPadding(int left, int right, int bottom, int top) {
        this.padding[0] = left;
        this.padding[1] = right;
        this.padding[2] = bottom;
        this.padding[3] = top;
    }

    public void hideName() {
        this.nameShown = false;
    }

    public Tab getTab() {
        return this.tab;
    }

    public T widget() {
        return this.widget;
    }

    public int[] getPadding() {
        return this.padding;
    }

    public int getLeftPadding() {
        return this.padding[0];
    }

    public int getRightPadding() {
        return this.padding[1];
    }

    public int getBottomPadding() {
        return this.padding[2];
    }

    public int getTopPadding() {
        return this.padding[3];
    }


}
