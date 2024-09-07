package net.refractionapi.refraction.data;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.Entity;

import java.util.HashMap;

/**
 * Class syncer <br>
 * Register with {@link Serializable#setSynced()} <br>
 * You need to have an empty constructor in the target class! - might add constructor support, idk, --Zeus
 */
public interface Serializable {

    HashMap<Class<? extends Serializable>, SerializableHandler<?>> serializers = new HashMap<>();

    default void sync(Entity sync) {
        if (!serializers.containsKey(this.getClass()) || !serializers.get(this.getClass()).HANDLER.containsKey(this)) {
            throw new IllegalStateException("No handler for " + this);
        }
        serializers.get(this.getClass()).sync(this, sync);
    }

    default void setSynced() {
        serializers.computeIfAbsent(this.getClass(), (c) -> new SerializableHandler<>()).add(this);
    }

    void write(FriendlyByteBuf buf);

    void read(FriendlyByteBuf buf);

}
