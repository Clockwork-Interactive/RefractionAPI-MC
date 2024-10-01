package net.refractionapi.refraction.data;

import net.minecraft.network.FriendlyByteBuf;
import net.refractionapi.refraction.Refraction;

import java.util.HashMap;

public class ClientSyncer<T extends Syncable> {

    public static final HashMap<Class<? extends Syncable>, ClientSyncer<?>> SERIALIZERS = new HashMap<>();
    private final HashMap<Integer, T> cache = new HashMap<>();
    private final Class<? extends Syncable> clazz;

    public ClientSyncer(Class<? extends Syncable> clazz) {
        this.clazz = clazz;
    }

    public static void handle(Class<? extends Syncable> clazz, int id, FriendlyByteBuf buf) {
        ClientSyncer<?> serializer = SERIALIZERS.computeIfAbsent(clazz, (s) -> new ClientSyncer<>(clazz));
        serializer.handleSerializer(clazz, id, buf);
    }

    @SuppressWarnings("unchecked")
    public void handleSerializer(Class<? extends Syncable> clazz, int id, FriendlyByteBuf buf) {
        this.cache.computeIfAbsent(id, (i) -> {
            T instance;
            try {
                instance = (T) clazz.newInstance();
            } catch (InstantiationException | IllegalAccessException e) {
                throw new InstantiationError("Failed to instantiate class %s, no empty constructor located".formatted(clazz.getName()));
            }
            return instance;
        }).read(buf);
    }

}
