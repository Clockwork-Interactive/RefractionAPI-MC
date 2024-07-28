package net.refractionapi.refraction.networking.S2C;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;
import net.refractionapi.refraction.client.ClientData;
import net.refractionapi.refraction.networking.Packet;
import net.refractionapi.refraction.screen.ScreenBuilder;

public class SetScreenS2CPacket extends Packet {

    private final ScreenBuilder<?> builder;
    private final CompoundTag serialized;

    public SetScreenS2CPacket(ScreenBuilder<?> builder, CompoundTag serialized) {
        this.builder = builder;
        this.serialized = serialized;
    }

    public SetScreenS2CPacket(FriendlyByteBuf buf) {
        this.builder = ScreenBuilder.get(buf.readUtf());
        this.serialized = buf.readNbt();
    }

    @Override
    public void toBytes(FriendlyByteBuf buf) {
        buf.writeUtf(this.builder.getId());
        buf.writeNbt(this.serialized);
    }

    @Override
    public void handle(NetworkEvent.Context context) {
        context.enqueueWork(() -> ClientData.screenHandler.setScreen(this.builder, this.builder.deserialize(this.serialized)));
        context.setPacketHandled(true);
    }

}
