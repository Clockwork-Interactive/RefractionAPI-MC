package net.refractionapi.refraction.networking.S2C;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;
import net.refractionapi.refraction.networking.Packet;
import net.refractionapi.refraction.sound.TickableSoundRegistry;

public class AttachTickableSoundS2CPacket extends Packet {

    private final String sound;
    private final CompoundTag serialized;

    public AttachTickableSoundS2CPacket(String sound, CompoundTag serialized) {
        this.sound = sound;
        this.serialized = serialized;
    }

    public AttachTickableSoundS2CPacket(FriendlyByteBuf buf) {
        this.sound = buf.readUtf();
        this.serialized = buf.readNbt();
    }

    @Override
    public void toBytes(FriendlyByteBuf buf) {
        buf.writeUtf(this.sound);
        buf.writeNbt(this.serialized);
    }

    @Override
    public void handle(NetworkEvent.Context context) {
        context.enqueueWork(() ->
                TickableSoundRegistry.attach(this.sound, this.serialized)
        );
        context.setPacketHandled(true);
    }

}
