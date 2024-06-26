package net.refractionapi.refraction.networking.C2S;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;
import net.refractionapi.refraction.interaction.InteractionBuilder;
import net.refractionapi.refraction.networking.Packet;

public class SyncInteractionC2SPacket extends Packet {

    private final String id;
    private final CompoundTag tag;

    public SyncInteractionC2SPacket(String id, CompoundTag tag) {
        this.id = id;
        this.tag = tag;
    }

    public SyncInteractionC2SPacket(FriendlyByteBuf buf) {
        this.id = buf.readUtf();
        this.tag = buf.readNbt();
    }

    @Override
    public void toBytes(FriendlyByteBuf buf) {
        buf.writeUtf(this.id);
        buf.writeNbt(this.tag);
    }

    @Override
    public void handle(NetworkEvent.Context context) {
        context.enqueueWork(() -> InteractionBuilder.getBuilder(this.id).ifPresent((builder) -> builder.handleServer(context.getSender(), this.tag)));
        context.setPacketHandled(true);
    }

}
