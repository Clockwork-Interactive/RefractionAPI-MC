package net.refractionapi.refraction.gui.tools;

import imgui.ImGui;
import net.refractionapi.refraction.gui.RIMTool;

public class RIMHealth extends RIMTool {
    private double tps = 20;
    private int players = 0;
    private int loadedChunks = 0;
    private int loadedEntities = 0;

    @Override
    public void init() {
        this.api().configure((channel -> {
            channel.registerListener("health", (player, buf) -> {
                this.tps = buf.readDouble();
                this.players = buf.readInt();
                this.loadedEntities = buf.readInt();
                this.loadedChunks = buf.readInt();
                return 1;
            });
        }));
    }

    @Override
    public void render() {
        ImGui.begin("Server Health");
        ImGui.text("TPS: %.1f".formatted(this.tps));
        ImGui.text("Players: %d".formatted(this.players));
        ImGui.text("Loaded Chunks: %d".formatted(this.loadedChunks));
        ImGui.text("Loaded Entities: %d".formatted(this.loadedEntities));
        ImGui.end();
    }

    @Override
    public String name() {
        return "Server Health";
    }

    @Override
    public String group() {
        return NETWORK;
    }
}
