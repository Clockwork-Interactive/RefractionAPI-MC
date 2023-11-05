package net.refractionapi.refraction.networking.S2C;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;
import net.refractionapi.refraction.client.ClientData;
import net.refractionapi.refraction.networking.Packet;

public class EnablePlayerMovementS2CPacket extends Packet {

    private final boolean canMove;

    public EnablePlayerMovementS2CPacket(boolean canMove) {
        this.canMove = canMove;
    }

    public EnablePlayerMovementS2CPacket(FriendlyByteBuf buf) {
        canMove = buf.readBoolean();
    }

    @Override
    public void toBytes(FriendlyByteBuf buf) {
        buf.writeBoolean(canMove);
    }

    @Override
    public void handle(NetworkEvent.Context context) {
        context.enqueueWork(() -> ClientData.canMove = this.canMove);
        context.setPacketHandled(true);
    }
}
