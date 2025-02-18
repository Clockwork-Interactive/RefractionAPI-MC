package net.refractionapi.refraction.gui;

import imgui.internal.ImGui;
import net.refractionapi.refraction.debug.RDebugRenderer;

public class RIMDebuggers extends RIMTool {
    @Override
    public void init() {

    }

    @Override
    public void render() {
        ImGui.begin("Debuggers");
        int id = 0;
        for (String renderer : RDebugRenderer.getRenderers()) {
            if (ImGui.collapsingHeader(renderer)) {
                if (ImGui.checkbox("Enabled##%d".formatted(id), RDebugRenderer.isEnabled(renderer))) RDebugRenderer.toggle(renderer);
                RDebugRenderer.getRenderer(renderer).renderGUI();
            }
            id++;
        }
        ImGui.end();
    }

    @Override
    public String name() {
        return "Debuggers";
    }

    @Override
    public String group() {
        return "Debuggers";
    }
}
