package net.refractionapi.refraction.networking.S2C;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.event.network.CustomPayloadEvent;
import net.refractionapi.refraction.client.ClientData;
import net.refractionapi.refraction.networking.Packet;

public class EnablePlayerMovementS2CPacket extends Packet {

    private final boolean canMove;

    public EnablePlayerMovementS2CPacket(boolean canMove) {
        this.canMove = canMove;
    }

    public EnablePlayerMovementS2CPacket(FriendlyByteBuf buf) {
        this.canMove = buf.readBoolean();
    }

    @Override
    public void toBytes(FriendlyByteBuf buf) {
        buf.writeBoolean(this.canMove);
    }

    @Override
    public void handle(CustomPayloadEvent.Context context) {
        context.enqueueWork(() -> ClientData.canMove = this.canMove);
        context.setPacketHandled(true);
    }
}
