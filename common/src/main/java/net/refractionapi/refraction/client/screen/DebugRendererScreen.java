package net.refractionapi.refraction.client.screen;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Checkbox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
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
            Checkbox checkbox = Checkbox.builder(Component.literal(renderer.toUpperCase(Locale.ROOT)), this.font)
                    .pos(this.width / 2 - 20, y)
                    .selected(RDebugRenderer.isEnabled(renderer))
                    .onValueChange((checkbox1, value) -> RDebugRenderer.toggle(renderer)).build();
            this.renderers.add(checkbox);
            this.addRenderableWidget(checkbox);
            y += 22;
        }
    }

    @Override
    public void render(GuiGraphics pGuiGraphics, int pMouseX, int pMouseY, float pPartialTick) {
        this.renderBackground(pGuiGraphics, pMouseX, pMouseY, pPartialTick);

        for (Checkbox checkbox : this.renderers) {
            checkbox.render(pGuiGraphics, pMouseX, pMouseY, pPartialTick);
        }
    }

}
