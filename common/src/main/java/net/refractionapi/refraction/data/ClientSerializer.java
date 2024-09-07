package net.refractionapi.refraction.data;

import net.minecraft.network.FriendlyByteBuf;
import net.refractionapi.refraction.Refraction;

import java.util.HashMap;

public class ClientSerializer<T extends Serializable> {

    public static final HashMap<Class<? extends Serializable>, ClientSerializer<?>> SERIALIZERS = new HashMap<>();
    private final HashMap<Integer, T> cache = new HashMap<>();

    public ClientSerializer(Class<? extends Serializable> clazz) {
        SERIALIZERS.put(clazz, this);
    }

    private static ClientSerializer<?> register(Class<? extends Serializable> clazz, ClientSerializer<?> serializer) {
        return SERIALIZERS.put(clazz, serializer);
    }

    public static void handle(Class<? extends Serializable> clazz, int id, FriendlyByteBuf buf) {
        ClientSerializer<?> serializer = SERIALIZERS.computeIfAbsent(clazz, (s) -> register(clazz, new ClientSerializer<>(clazz)));
        if (serializer != null) {
            serializer.handleSerializer(clazz, id, buf);
        }
    }

    @SuppressWarnings("unchecked")
    public void handleSerializer(Class<? extends Serializable> clazz, int id, FriendlyByteBuf buf) {
        this.cache.computeIfAbsent(id, (i) -> {
            T instance = null;
            try {
                instance = (T) clazz.newInstance();
            } catch (InstantiationException | IllegalAccessException e) {
                Refraction.LOGGER.error("Failed to instantiate class {}, no empty constructor located", clazz.getName(), e);
            }
            return instance;
        }).read(buf);
    }

}
