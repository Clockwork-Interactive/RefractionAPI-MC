package net.refractionapi.refraction.networking.C2S;

import io.netty.buffer.Unpooled;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Player;
import net.refractionapi.refraction.feature.channel.TwoWayIntermediary;
import net.refractionapi.refraction.networking.Packet;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;
import java.util.function.Consumer;

public class TwoWayC2SPacket extends Packet {
    private final UUID uuid;
    private final FriendlyByteBuf buf;

    public TwoWayC2SPacket(UUID uuid, FriendlyByteBuf buf) {
        this.uuid = uuid;
        this.buf = buf;
    }

    public TwoWayC2SPacket(FriendlyByteBuf buf) {
        this.uuid = buf.readUUID();
        this.buf = new FriendlyByteBuf(Unpooled.copiedBuffer(buf.readByteArray()));
    }

    @Override
    public void write(FriendlyByteBuf buf) {
        buf.writeUUID(this.uuid);
        buf.writeByteArray(this.buf.array());
    }

    @Override
    public void handle(@Nullable Player player, Consumer<Runnable> context) {
        context.accept(() -> {
            if (player == null) return;
            TwoWayIntermediary.instance(true).read(player, this.uuid, this.buf);
        });
    }
}
