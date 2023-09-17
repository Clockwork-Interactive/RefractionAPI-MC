package net.refractionapi.refraction.networking;

import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;

public abstract class Packet {
    public Packet() {
    }

    public Packet(PacketBuffer buf) {
    }

    public abstract void toBytes(PacketBuffer buf);

    public abstract void handle(NetworkEvent.Context context);
}
