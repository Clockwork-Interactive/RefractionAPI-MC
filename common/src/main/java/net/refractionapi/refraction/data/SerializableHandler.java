package net.refractionapi.refraction.data;

import io.netty.buffer.Unpooled;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.refractionapi.refraction.networking.S2C.SerializerS2CPacket;
import net.refractionapi.refraction.platform.RefractionServices;

import java.util.HashMap;

public class SerializableHandler<C extends Syncable> {

    protected final HashMap<C, Integer> HANDLER = new HashMap<>();

    public SerializableHandler() {

    }

    public <T> SerializableHandler<C> add(T data) {
        this.HANDLER.put((C) data, this.HANDLER.size());
        return this;
    }

    @SuppressWarnings("unchecked")
    public void sync(Syncable data, Entity syncTo) {
        if (syncTo instanceof ServerPlayer serverPlayer) {
            FriendlyByteBuf buf = new FriendlyByteBuf(Unpooled.buffer());
            data.write(buf);
            RefractionServices.MESSAGES.sendPlayer(new SerializerS2CPacket(data.getClass(), this.HANDLER.get((C) data), buf), serverPlayer);
        }
    }


}
