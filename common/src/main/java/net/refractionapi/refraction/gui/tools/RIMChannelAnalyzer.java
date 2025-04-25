package net.refractionapi.refraction.gui.tools;

import imgui.ImGui;
import net.refractionapi.refraction.feature.channel.NamedAPI;
import net.refractionapi.refraction.feature.channel.TwoWayChannel;
import net.refractionapi.refraction.feature.channel.TwoWayIntermediary;
import net.refractionapi.refraction.gui.RIMTool;
import net.refractionapi.refraction.util.Pair;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class RIMChannelAnalyzer extends RIMTool {
    @Override
    public void init() {

    }

    @Override
    public void render() {
        ImGui.begin("Channels");
        List<Pair<UUID, Optional<TwoWayChannel>>> channels = TwoWayIntermediary.instance(false).channels();
        if (ImGui.beginTable("Channels", 3)) {
            ImGui.tableSetupColumn("UUID");
            ImGui.tableSetupColumn("NamedAPI");
            ImGui.tableSetupColumn("Status");
            ImGui.tableHeadersRow();
            for (Pair<UUID, Optional<TwoWayChannel>> channel : channels) {
                ImGui.tableNextRow();
                ImGui.tableSetColumnIndex(0);
                ImGui.text(channel.getFirst().toString());
                ImGui.tableSetColumnIndex(1);
                ImGui.text(NamedAPI.getChannel(channel.getFirst()).map(Object::toString).orElse("null"));
                ImGui.tableSetColumnIndex(2);
                ImGui.text(channel.getSecond().map((c) -> "Active").orElse("Inactive"));
            }
            ImGui.endTable();
        }
        ImGui.end();
    }

    @Override
    public String name() {
        return "Channel Analyzer";
    }

    @Override
    public String group() {
        return "Networking";
    }
}
