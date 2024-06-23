package net.refractionapi.refraction.networking;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.event.network.CustomPayloadEvent;

public abstract class Packet {

    public Packet() {
    }

    public Packet(FriendlyByteBuf buf) {
    }

    public abstract void toBytes(FriendlyByteBuf buf);

    public abstract void handle(CustomPayloadEvent.Context context);

}
