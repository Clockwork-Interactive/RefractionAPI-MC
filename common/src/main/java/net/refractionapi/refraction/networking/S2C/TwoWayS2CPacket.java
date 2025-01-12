package net.refractionapi.refraction.networking.S2C;

import io.netty.buffer.Unpooled;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Player;
import net.refractionapi.refraction.client.ClientData;
import net.refractionapi.refraction.feature.channel.TwoWayIntermediary;
import net.refractionapi.refraction.networking.Packet;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;
import java.util.function.Consumer;

public class TwoWayS2CPacket extends Packet {
    private final UUID uuid;
    private final FriendlyByteBuf header;
    private final FriendlyByteBuf buf;

    public TwoWayS2CPacket(UUID uuid, FriendlyByteBuf header, FriendlyByteBuf buf) {
        this.uuid = uuid;
        this.header = header;
        this.buf = buf;
    }

    public TwoWayS2CPacket(FriendlyByteBuf buf) {
        this.uuid = buf.readUUID();
        this.header = new FriendlyByteBuf(Unpooled.copiedBuffer(buf.readByteArray()));
        this.buf = new FriendlyByteBuf(Unpooled.copiedBuffer(buf.readByteArray()));
    }

    @Override
    public void write(FriendlyByteBuf buf) {
        buf.writeUUID(this.uuid);
        buf.writeByteArray(this.header.array());
        buf.writeByteArray(this.buf.array());
    }

    @Override
    public void handle(@Nullable Player player, Consumer<Runnable> context) {
        context.accept(() -> {
            if (player == null) return;
            TwoWayIntermediary.instance(false).read(ClientData.getPlayer(), this.uuid, this.header, this.buf);
        });
    }
}
