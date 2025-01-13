package net.refractionapi.refraction.networking.S2C;

import io.netty.buffer.Unpooled;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Player;
import net.refractionapi.refraction.debug.RDebugRenderer;
import net.refractionapi.refraction.networking.Packet;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;

public class DebugRendererS2CPacket extends Packet {
    private final String router;
    private final FriendlyByteBuf buf;

    public DebugRendererS2CPacket(String router, FriendlyByteBuf buf) {
        this.router = router;
        this.buf = buf;
    }

    public DebugRendererS2CPacket(FriendlyByteBuf buf) {
        this.router = buf.readUtf();
        this.buf = new FriendlyByteBuf(Unpooled.copiedBuffer(buf.readByteArray()));
    }

    @Override
    public void write(FriendlyByteBuf buf) {
        buf.writeUtf(this.router);
        buf.writeByteArray(this.buf.array());
    }

    @Override
    public void handle(@Nullable Player player, Consumer<Runnable> context) {
        context.accept(() -> RDebugRenderer.route(this.router, this.buf));
    }
}
