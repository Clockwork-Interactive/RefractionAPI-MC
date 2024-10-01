package net.refractionapi.refraction.networking.S2C;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Player;
import net.refractionapi.refraction.data.ClientSyncer;
import net.refractionapi.refraction.data.Syncable;
import net.refractionapi.refraction.networking.Packet;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;

public class SerializerS2CPacket extends Packet {

    private final Class<? extends Syncable> clazz;
    private final int id;
    private final FriendlyByteBuf buf;

    public SerializerS2CPacket(Class<? extends Syncable> clazz, int id, FriendlyByteBuf buf) {
        this.clazz = clazz;
        this.id = id;
        this.buf = buf;
    }

    @SuppressWarnings("unchecked")
    public SerializerS2CPacket(FriendlyByteBuf buf) throws ClassNotFoundException {
        this.clazz = (Class<? extends Syncable>) Class.forName(buf.readUtf());
        this.id = buf.readInt();
        this.buf = buf;
    }

    @Override
    public void write(FriendlyByteBuf buf) {
        buf.writeUtf(this.clazz.getName());
        buf.writeInt(this.id);
        buf.writeBytes(this.buf);
    }

    @Override
    public void handle(@Nullable Player player, Consumer<Runnable> context) {
        context.accept(() -> ClientSyncer.handle(this.clazz, this.id, this.buf));
    }

}
