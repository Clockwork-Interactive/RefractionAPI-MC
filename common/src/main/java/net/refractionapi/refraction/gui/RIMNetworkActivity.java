package net.refractionapi.refraction.gui;

import imgui.ImGui;
import imgui.extension.implot.ImPlot;
import net.minecraft.client.Minecraft;
import net.minecraft.network.protocol.PacketType;

import java.util.function.BooleanSupplier;
import java.util.stream.IntStream;

public class RIMNetworkActivity extends RIMTool {
    private int ticks = 0;
    @SuppressWarnings("rawtypes")
    private PacketType[][] packetsRec = new PacketType[200][0];
    private PacketType[][] packetsSent = new PacketType[200][0];
    private BooleanSupplier paused = () -> Minecraft.getInstance().isPaused();

    @Override
    public void init() {
    }

    @Override
    public void tick() {
        if (this.paused.getAsBoolean()) return;
        this.ticks++;
        this.ticks %= 200;
        this.packetsRec[(this.ticks + 1) % 200] = new PacketType[0];
        this.packetsSent[(this.ticks + 1) % 200] = new PacketType[0];
    }

    public void receivePacket(PacketType<?> packet) {
        if (this.paused.getAsBoolean()) return;
        PacketType<?>[] currentPackets = this.packetsRec[this.ticks];
        PacketType<?>[] newPackets = new PacketType[currentPackets.length + 1];
        System.arraycopy(currentPackets, 0, newPackets, 0, currentPackets.length);
        newPackets[currentPackets.length] = packet;
        this.packetsRec[this.ticks] = newPackets;
    }

    public void sendPacket(PacketType<?> packet) {
        if (this.paused.getAsBoolean()) return;
        PacketType<?>[] currentPackets = this.packetsSent[this.ticks];
        PacketType<?>[] newPackets = new PacketType[currentPackets.length + 1];
        System.arraycopy(currentPackets, 0, newPackets, 0, currentPackets.length);
        newPackets[currentPackets.length] = packet;
        this.packetsSent[this.ticks] = newPackets;
    }

    @Override
    public void render() {
        ImGui.begin("Network Activity");
        if (!ImPlot.beginPlot("Network Activity", "Ticks", "Packets")) {
            ImGui.end();
            return;
        }
        Integer[] recPacketsForTick = IntStream.range(0, 200).mapToObj(i -> this.packetsRec[i] == null ? 0 : this.packetsRec[i].length).toArray(Integer[]::new);
        Integer[] sentPacketsForTick = IntStream.range(0, 200).mapToObj(i -> this.packetsSent[i] == null ? 0 : this.packetsSent[i].length).toArray(Integer[]::new);
        Integer[] ticks = IntStream.range(0, 200).boxed().toArray(Integer[]::new);
        ImPlot.plotLine("Incoming Packets", ticks, recPacketsForTick);
        ImPlot.plotLine("Outgoing Packets", ticks, sentPacketsForTick);
        ImPlot.endPlot();
        ImGui.end();
    }

    @Override
    public String name() {
        return "Network Activity";
    }

    @Override
    public String group() {
        return "Networking";
    }
}
