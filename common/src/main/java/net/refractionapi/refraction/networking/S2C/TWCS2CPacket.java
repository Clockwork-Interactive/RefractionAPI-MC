package net.refractionapi.refraction.networking.S2C;

import io.netty.buffer.Unpooled;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Player;
import net.refractionapi.refraction.feature.twc.TWCMiddleWare;
import net.refractionapi.refraction.networking.Packet;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;
import java.util.function.Consumer;

public class TWCS2CPacket extends Packet {
    final UUID uuid;
    final FriendlyByteBuf buf;

    public TWCS2CPacket(UUID uuid, FriendlyByteBuf buf) {
        this.uuid = uuid;
        this.buf = buf;
    }

    public TWCS2CPacket(FriendlyByteBuf buf) {
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
            TWCMiddleWare.instance().receiveMessage(player, this.uuid, this.buf);
        });
    }
}
