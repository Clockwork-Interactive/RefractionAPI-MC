package net.refractionapi.refraction.gui.tools;

import imgui.ImGui;
import imgui.flag.ImGuiInputTextFlags;
import imgui.type.ImString;
import net.refractionapi.refraction.gui.RIMTool;
import net.refractionapi.refraction.gui.cli.CLI;

public class RIMCli extends RIMTool {
    private final CLI cli = new CLI();
    private ImString input = new ImString();

    @Override
    public void init() {
    }

    @Override
    public void render() {
        ImGui.begin("Refraction CLI");
        // window history + command input
        for (String s : cli.history()) {
            ImGui.text(s);
        }
        if (ImGui.inputText("<$", input, ImGuiInputTextFlags.EnterReturnsTrue)) {
            if (input.get().isEmpty()) {
                ImGui.end();
                return;
            }
            String[] args = input.get().split(" ");
            cli.runCommand(args);
            input.set("");
        }
        // auto focus text
        if (!ImGui.isMouseClicked(0) && !ImGui.isMouseDown(0) && ImGui.isWindowFocused() && ImGui.isItemFocused()) {
            ImGui.setKeyboardFocusHere(-1);
        }
        ImGui.end();
    }

    @Override
    public String name() {
        return "CLI";
    }

    @Override
    public String group() {
        return "Debuggers";
    }
}
