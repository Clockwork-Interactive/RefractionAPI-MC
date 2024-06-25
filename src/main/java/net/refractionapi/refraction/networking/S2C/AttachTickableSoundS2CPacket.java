package net.refractionapi.refraction.networking.S2C;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.event.network.CustomPayloadEvent;
import net.refractionapi.refraction.client.ClientData;
import net.refractionapi.refraction.networking.Packet;
import net.refractionapi.refraction.sound.TickableSoundRegistry;

public class AttachTickableSoundS2CPacket extends Packet {

    private final String sound;
    private final LivingEntity living;
    private final CompoundTag serialized;
    private final String cacheName;

    public AttachTickableSoundS2CPacket(String sound, LivingEntity living, CompoundTag serialized, String cacheName) {
        this.sound = sound;
        this.living = living;
        this.serialized = serialized;
        this.cacheName = cacheName;
    }

    public AttachTickableSoundS2CPacket(String sound, LivingEntity living, CompoundTag serialized) {
        this.sound = sound;
        this.living = living;
        this.serialized = serialized;
        this.cacheName = "";
    }

    public AttachTickableSoundS2CPacket(FriendlyByteBuf buf) {
        this.sound = buf.readUtf();
        this.living = ClientData.getEntity(buf.readInt());
        this.serialized = buf.readNbt();
        this.cacheName = buf.readUtf();
    }

    @Override
    public void toBytes(FriendlyByteBuf buf) {
        buf.writeUtf(this.sound);
        buf.writeInt(this.living.getId());
        buf.writeNbt(this.serialized);
        buf.writeUtf(this.cacheName);
    }

    @Override
    public void handle(CustomPayloadEvent.Context context) {
        context.enqueueWork(() -> TickableSoundRegistry.attach(this.sound, this.living, this.serialized, this.cacheName));
        context.setPacketHandled(true);
    }

}
