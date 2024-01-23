package net.refractionapi.refraction.networking.S2C;

import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;
import net.refractionapi.refraction.client.ClientData;
import net.refractionapi.refraction.networking.Packet;

public class SetFOVS2CPacket extends Packet {

    private final int fov;

    public SetFOVS2CPacket(int fov) {
        this.fov = fov;
    }

    public SetFOVS2CPacket(FriendlyByteBuf buf) {
        this.fov = buf.readInt();
    }

    @Override
    public void toBytes(FriendlyByteBuf buf) {
        buf.writeInt(this.fov);
    }

    @Override
    public void handle(NetworkEvent.Context context) {
        context.enqueueWork(() -> {
            ClientData.FOV = this.fov;
        });
        context.setPacketHandled(true);
    }
}
