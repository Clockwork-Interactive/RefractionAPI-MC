package net.refractionapi.refraction.networking.S2C;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Player;
import net.refractionapi.refraction.client.ClientData;
import net.refractionapi.refraction.networking.Packet;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;

public class SendScreenDataS2CPacket extends Packet {

    private final CompoundTag tag;

    public SendScreenDataS2CPacket(CompoundTag tag) {
        this.tag = tag;
    }

    public SendScreenDataS2CPacket(FriendlyByteBuf buf) {
        this.tag = buf.readNbt();
    }

    @Override
    public void write(FriendlyByteBuf buf) {
        buf.writeNbt(this.tag);
    }

    @Override
    public void handle(@Nullable Player player, Consumer<Runnable> context) {
        context.accept(() -> ClientData.screenHandler.handleServerEvent(this.tag));
    }

}
