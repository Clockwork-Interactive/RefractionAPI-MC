package net.refractionapi.refraction.networking.S2C;

import io.netty.buffer.Unpooled;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Player;
import net.refractionapi.refraction.feature.data.ClientSyncer;
import net.refractionapi.refraction.feature.data.Syncable;
import net.refractionapi.refraction.networking.Packet;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;

public class SerializerS2CPacket extends Packet {

    private final Class<? extends Syncable<?>> clazz;
    private final int id;
    private final FriendlyByteBuf buf;
    private final FriendlyByteBuf constArgs;

    public SerializerS2CPacket(Class<? extends Syncable<?>> clazz, int id, FriendlyByteBuf buf, FriendlyByteBuf constArgs) {
        this.clazz = clazz;
        this.id = id;
        this.buf = buf;
        this.constArgs = constArgs;
    }

    @SuppressWarnings("unchecked")
    public SerializerS2CPacket(FriendlyByteBuf buf) throws ClassNotFoundException {
        this.clazz = (Class<? extends Syncable<?>>) Class.forName(buf.readUtf());
        this.id = buf.readInt();
        this.buf = new FriendlyByteBuf(Unpooled.copiedBuffer(buf.readByteArray()));
        this.constArgs = new FriendlyByteBuf(Unpooled.copiedBuffer(buf.readByteArray()));
    }

    @Override
    public void write(FriendlyByteBuf buf) {
        buf.writeUtf(this.clazz.getName());
        buf.writeInt(this.id);
        buf.writeByteArray(this.buf.array());
        buf.writeByteArray(this.constArgs.array());
    }

    @Override
    public void handle(@Nullable Player player, Consumer<Runnable> context) {
        context.accept(() -> ClientSyncer.handle(this.clazz, this.id, this.buf, this.constArgs));
    }

}
