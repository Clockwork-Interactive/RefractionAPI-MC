package net.refractionapi.refraction.feature.data;

import io.netty.buffer.Unpooled;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.refractionapi.refraction.networking.S2C.SerializerS2CPacket;
import net.refractionapi.refraction.platform.RefractionServices;

import java.util.HashMap;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;

public class SerializableHandler<C extends Syncable<C>> {

    protected final HashMap<C, Integer> HANDLER = new HashMap<>();
    protected BiFunction<C, FriendlyByteBuf, Object[]> deserializer = Syncable::deserialize;
    protected BiConsumer<C, FriendlyByteBuf> serializer = Syncable::serialize;

    public SerializableHandler() {

    }

    public Object[] get(C obj, FriendlyByteBuf buf) {
        return buf == null ? new Object[]{} : this.deserializer.apply(obj, buf);
    }

    public <T> SerializableHandler<C> add(T data) {
        this.HANDLER.put((C) data, this.HANDLER.size());
        return this;
    }

    @SuppressWarnings("unchecked")
    public void sync(Syncable<?> data, Entity syncTo) {
        if (syncTo instanceof ServerPlayer serverPlayer) {
            // I split them up cause networking was being a pain
            FriendlyByteBuf buf = new FriendlyByteBuf(Unpooled.buffer());
            FriendlyByteBuf constArgs = new FriendlyByteBuf(Unpooled.buffer());
            this.serializer.accept((C) data, constArgs);
            data.write(buf);
            RefractionServices.MESSAGES.sendPlayer(new SerializerS2CPacket(
                    (Class<? extends Syncable<?>>) data.getClass(),
                    this.HANDLER.get((C) data),
                    buf,
                    constArgs
            ), serverPlayer);
        }
    }

    public static Class<?>[] formClassArray(Object... objects) {
        Class<?>[] classes = new Class[objects.length];
        for (int i = 0; i < objects.length; i++) {
            classes[i] = objects.getClass();
        }
        return classes;
    }

}
