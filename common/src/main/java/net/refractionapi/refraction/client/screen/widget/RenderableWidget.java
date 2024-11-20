package net.refractionapi.refraction.client.screen.widget;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;

public class RenderableWidget extends AbstractWidget {

    private Renderable renderable;

    public RenderableWidget(int x, int y, int xSize, int ySize, Component component) {
        super(x, y, xSize, ySize, component);
    }

    public RenderableWidget setRenderable(Renderable renderable) {
        this.renderable = renderable;
        return this;
    }

    @Override
    protected void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
        if (this.renderable != null) {
            this.renderable.render(guiGraphics, mouseX, mouseY, partialTicks);
        }
    }

    @Override
    protected void updateWidgetNarration(NarrationElementOutput var1) {

    }


    @FunctionalInterface
    public interface Renderable {
        void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks);
    }

}
