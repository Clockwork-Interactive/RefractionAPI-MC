package net.refractionapi.refraction.networking;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

public abstract class Packet {

    public Packet() {
    }

    public Packet(FriendlyByteBuf buf) {
    }

    public abstract void toBytes(FriendlyByteBuf buf);

    public abstract void handle(NetworkEvent.Context context);

}
