package net.refractionapi.refraction.feature.data;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.Entity;
import net.refractionapi.refraction.feature.examples.data.SyncedDataExample;

import java.util.HashMap;

/**
 * Class syncer <br>
 * Register with {@link Syncable#setSynced()} in the constructor <br>
 * See {@link SyncedDataExample}
 */
public interface Syncable<C extends Syncable<C>> {

    HashMap<Class<? extends Syncable<?>>, SerializableHandler<?>> serializers = new HashMap<>();

    default void sync(Entity sync) {
        if (!serializers.containsKey(this.getClass()) || !serializers.get(this.getClass()).HANDLER.containsKey(this)) {
            throw new IllegalStateException("No handler for " + this.getClass().getName());
        }
        serializers.get(this.getClass()).sync(this, sync);
    }

    @SuppressWarnings("unchecked")
    default SerializableHandler<C> syncBuilder() {
        return (SerializableHandler<C>) serializers.computeIfAbsent((Class<? extends Syncable<?>>) this.getClass(), (c) -> new SerializableHandler<>()).add(this);
    }

    default void setSynced() {
        try {
            this.getClass().getConstructor(FriendlyByteBuf.class).newInstance((FriendlyByteBuf) null);
            return;
        } catch (Exception ignored) {
        }
        this.syncBuilder();
    }

    void write(FriendlyByteBuf buf);

    void read(FriendlyByteBuf buf);

}
