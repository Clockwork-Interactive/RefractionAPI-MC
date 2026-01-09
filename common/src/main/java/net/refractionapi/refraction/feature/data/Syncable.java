package net.refractionapi.refraction.feature.data;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.LevelAccessor;
import net.refractionapi.refraction.feature.examples.data.SyncedDataExample;

import java.util.concurrent.ConcurrentHashMap;

/**
 * Class syncer <br>
 * Register with {@link Syncable#setSynced()} in the constructor <br>
 * See {@link SyncedDataExample}
 */
public interface Syncable<C extends Syncable<C>> {
    ConcurrentHashMap<Class<? extends Syncable<?>>, SerializableHandler<?>> serializers = new ConcurrentHashMap<>();

    default void sync(Entity sync) {
        if (!serializers.containsKey(this.getClass()) || !serializers.get(this.getClass()).HANDLER.containsKey(this)) {
            throw new IllegalStateException("this.setSynced() has not been called in a constructor for %s".formatted(this.getClass().getName()));
        }
        serializers.get(this.getClass()).sync(this, sync);
    }

    default void sync(LevelAccessor level) {
        if (!(level instanceof ServerLevel serverLevel)) return;
        sync(serverLevel.getServer());
    }

    default void sync(MinecraftServer server) {
        for (ServerPlayer player : server.getPlayerList().getPlayers()) {
            this.sync(player);
        }
    }

    @SuppressWarnings("unchecked")
    default int getSyncID() {
        return this.syncableHandler().getID((C) this);
    }

    @SuppressWarnings("unchecked")
    default SerializableHandler<C> syncableHandler() {
        if (!serializers.containsKey(this.getClass())) {
            throw new IllegalStateException("this.setSynced() has not been called in a constructor for %s".formatted(this.getClass().getName()));
        }
        return (SerializableHandler<C>) serializers.get(this.getClass());
    }

    @SuppressWarnings("unchecked")
    default SerializableHandler<C> setSynced() {
        return (SerializableHandler<C>) serializers.computeIfAbsent((Class<? extends Syncable<?>>) this.getClass(), (c) -> new SerializableHandler<>()).add(this);
    }

    default void serialize(FriendlyByteBuf buf) {

    }

    default Object[] deserialize(FriendlyByteBuf buf) {
        return new Object[]{};
    }

    void write(FriendlyByteBuf buf);

    void read(FriendlyByteBuf buf);

    default void onSync(FriendlyByteBuf buf, int id) {
        this.read(buf);
    }
}
