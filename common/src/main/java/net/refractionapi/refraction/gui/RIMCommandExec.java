package net.refractionapi.refraction.gui;

import imgui.ImGui;
import imgui.type.ImString;

public class RIMCommandExec extends RIMTool {
    private final ImString command = new ImString(32500);

    @Override
    public void init() {

    }

    @Override
    public void render() {
        ImGui.begin("Command Executor");
        ImGui.inputTextMultiline("Command", command);
        if (ImGui.button("Execute")) {
            this.api().channel().send("command", (buf) -> buf.writeUtf(command.get()));
        }
        ImGui.end();
    }

    @Override
    public String name() {
        return "Command Executor";
    }

    @Override
    public String group() {
        return "Misc";
    }
}
