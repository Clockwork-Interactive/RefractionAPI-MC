package net.refractionapi.refraction.client.screen;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Checkbox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.refractionapi.refraction.client.screen.widget.RCheckbox;
import net.refractionapi.refraction.debug.RDebugRenderer;

import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

public class DebugRendererScreen extends Screen {

    private final Set<Checkbox> renderers = new HashSet<>();

    public DebugRendererScreen() {
        super(Component.literal("Debug Renderer"));
    }

    @Override
    protected void init() {
        int y = 0;
        for (String renderer : RDebugRenderer.getRenderers()) {
            Checkbox checkbox = new RCheckbox(this.width / 2 - 20, y, 20, 20,Component.literal(renderer.toUpperCase(Locale.ROOT)), RDebugRenderer.isEnabled(renderer), (checkbox1 -> RDebugRenderer.toggle(renderer)));
            this.renderers.add(checkbox);
            this.addRenderableWidget(checkbox);
            y += 22;
        }
    }

    @Override
    public void render(GuiGraphics pGuiGraphics, int pMouseX, int pMouseY, float pPartialTick) {
        this.renderBackground(pGuiGraphics);

        for (Checkbox checkbox : this.renderers) {
            checkbox.render(pGuiGraphics, pMouseX, pMouseY, pPartialTick);
        }
    }

}
