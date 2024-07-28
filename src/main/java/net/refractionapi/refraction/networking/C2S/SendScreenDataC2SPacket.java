package net.refractionapi.refraction.networking.C2S;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;
import net.refractionapi.refraction.data.RefractionData;
import net.refractionapi.refraction.networking.Packet;

public class SendScreenDataC2SPacket extends Packet {

    private final CompoundTag tag;

    public SendScreenDataC2SPacket(CompoundTag tag) {
        this.tag = tag;
    }

    public SendScreenDataC2SPacket(FriendlyByteBuf buf) {
        this.tag = buf.readNbt();
    }

    @Override
    public void toBytes(FriendlyByteBuf buf) {
        buf.writeNbt(this.tag);
    }

    @Override
    public void handle(NetworkEvent.Context context) {
        context.enqueueWork(() -> {
            ServerPlayer player = context.getSender();
            if (player == null) return;
            RefractionData data = RefractionData.get(player);
            data.getScreen().ifPresent((serverScreen -> {
                if (tag.contains("close"))
                    data.builder.onClose(player);
                else
                    data.builder.handleServer(data.screen, this.tag);
            }));
        });
        context.setPacketHandled(true);
    }

}
