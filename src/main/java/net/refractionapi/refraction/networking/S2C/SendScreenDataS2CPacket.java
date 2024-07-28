package net.refractionapi.refraction.networking.S2C;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;
import net.refractionapi.refraction.client.ClientData;
import net.refractionapi.refraction.networking.Packet;

public class SendScreenDataS2CPacket extends Packet {

    private final CompoundTag tag;

    public SendScreenDataS2CPacket(CompoundTag tag) {
        this.tag = tag;
    }

    public SendScreenDataS2CPacket(FriendlyByteBuf buf) {
        this.tag = buf.readNbt();
    }

    @Override
    public void toBytes(FriendlyByteBuf buf) {
        buf.writeNbt(this.tag);
    }

    @Override
    public void handle(NetworkEvent.Context context) {
        context.enqueueWork(() -> ClientData.screenHandler.handleServerEvent(this.tag));
        context.setPacketHandled(true);
    }

}
