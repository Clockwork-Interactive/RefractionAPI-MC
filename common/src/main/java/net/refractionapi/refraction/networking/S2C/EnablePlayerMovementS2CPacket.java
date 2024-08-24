package net.refractionapi.refraction.networking.S2C;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Player;
import net.refractionapi.refraction.client.ClientData;
import net.refractionapi.refraction.networking.Packet;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;

public class EnablePlayerMovementS2CPacket extends Packet {

    private final boolean canMove;

    public EnablePlayerMovementS2CPacket(boolean canMove) {
        this.canMove = canMove;
    }

    public EnablePlayerMovementS2CPacket(FriendlyByteBuf buf) {
        this.canMove = buf.readBoolean();
    }

    @Override
    public void write(FriendlyByteBuf buf) {
        buf.writeBoolean(this.canMove);
    }

    @Override
    public void handle(@Nullable Player player, Consumer<Runnable> context) {
        context.accept(() -> {
            ClientData.canMove = this.canMove;
        });
    }

}
