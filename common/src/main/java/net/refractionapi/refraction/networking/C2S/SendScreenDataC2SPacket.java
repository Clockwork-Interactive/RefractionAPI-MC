package net.refractionapi.refraction.networking.C2S;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.refractionapi.refraction.data.RefractionData;
import net.refractionapi.refraction.networking.Packet;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;

public class SendScreenDataC2SPacket extends Packet {

    private final CompoundTag tag;

    public SendScreenDataC2SPacket(CompoundTag tag) {
        this.tag = tag;
    }

    public SendScreenDataC2SPacket(FriendlyByteBuf buf) {
        this.tag = buf.readNbt();
    }

    @Override
    public void write(FriendlyByteBuf buf) {
        buf.writeNbt(this.tag);
    }

    @Override
    public void handle(@Nullable Player player, Consumer<Runnable> context) {
        context.accept(() -> {
            if (player instanceof ServerPlayer)
                RefractionData.get(player).getScreen().ifPresent((serverScreen -> {
                    if (tag.contains("close"))
                        RefractionData.get(player).builder.onClose(player);
                    else
                        RefractionData.get(player).builder.handleServer(serverScreen, this.tag);
                }));
        });
    }

}
