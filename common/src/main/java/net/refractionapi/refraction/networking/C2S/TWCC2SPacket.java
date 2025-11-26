package net.refractionapi.refraction.networking.C2S;

import io.netty.buffer.Unpooled;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Player;
import net.refractionapi.refraction.feature.twc.TWCMiddleWare;
import net.refractionapi.refraction.networking.Packet;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;
import java.util.function.Consumer;

public class TWCC2SPacket extends Packet {
    final UUID uuid;
    final FriendlyByteBuf buf;

    public TWCC2SPacket(UUID uuid, FriendlyByteBuf buf) {
        this.uuid = uuid;
        this.buf = buf;
    }

    public TWCC2SPacket(FriendlyByteBuf buf) {
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
