package net.refractionapi.refraction.networking.S2C;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;
import net.refractionapi.refraction.interaction.InteractionBuilder;
import net.refractionapi.refraction.networking.Packet;

public class HandleInteractionS2CPacket extends Packet {

    private final String id;
    private final CompoundTag tag;

    public HandleInteractionS2CPacket(String id, CompoundTag tag) {
        this.id = id;
        this.tag = tag;
    }

    public HandleInteractionS2CPacket(FriendlyByteBuf buf) {
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
        context.enqueueWork(() -> InteractionBuilder.getBuilder(this.id).ifPresent((builder) -> builder.handleClient(this.tag)));
        context.setPacketHandled(true);
    }

}
