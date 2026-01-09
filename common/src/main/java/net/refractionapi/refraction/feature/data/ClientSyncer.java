package net.refractionapi.refraction.feature.data;

import net.minecraft.network.FriendlyByteBuf;
import org.apache.commons.lang3.function.TriFunction;

import java.util.Arrays;
import java.util.HashMap;

public class ClientSyncer<T extends Syncable<?>> {
    public static final HashMap<Class<? extends Syncable<?>>, ClientSyncer<?>> SERIALIZERS = new HashMap<>();
    private static final HashMap<Class<? extends Syncable<?>>, TriFunction<Integer, FriendlyByteBuf, FriendlyByteBuf, ? extends Syncable<?>>> interceptors = new HashMap<>(); // can be unloaded, so it's static
    private final HashMap<Integer, T> cache = new HashMap<>();
    private final Class<T> clazz;

    public ClientSyncer(Class<?> clazz) {
        this.clazz = (Class<T>) clazz;
    }

    @SuppressWarnings("unchecked")
    public static void handle(Class<?> clazz, int id, FriendlyByteBuf buf, FriendlyByteBuf constArgs) {
        SERIALIZERS.computeIfAbsent(
                (Class<? extends Syncable<?>>) clazz,
                (s) -> new ClientSyncer<>(clazz)
        ).handleSerializer(clazz, id, buf, constArgs);
    }

    @SuppressWarnings("unchecked")
    public <C> void handleSerializer(Class<C> clazz, int id, FriendlyByteBuf buf, FriendlyByteBuf constArgs) {
        if (interceptors.containsKey(clazz)) {
            T interceptor = (T) interceptors.get(clazz).apply(id, buf, constArgs);
            if (interceptor != null) {
                interceptor.onSync(buf, id);
                return;
            }
        }
        this.cache.computeIfAbsent(id, (i) -> {
            var castClass = (Class<T>) clazz;
            T createConst = createConstructor(castClass, constArgs);
            if (createConst != null) return createConst;
            T byteBuf = createBufConstructor(castClass, constArgs);
            return byteBuf == null ? createEmpty(castClass) : byteBuf;
        }).onSync(buf, id);
    }

    public static <C extends Syncable<C>> void setInterceptor(Class<C> clazz, TriFunction<Integer, FriendlyByteBuf, FriendlyByteBuf, C> output) {
        interceptors.put(clazz, output);
    }

    private T createEmpty(Class<T> clazz) {
        try {
            return clazz.newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            throw new InstantiationError("Failed to instantiate class %s, no valid constructor found".formatted(clazz.getName()));
        }
    }

    private T createBufConstructor(Class<T> clazz, FriendlyByteBuf buf) {
        try {
            return clazz.getConstructor(FriendlyByteBuf.class).newInstance(buf);
        } catch (Exception ignored) {
            return null;
        }
    }

    private T createConstructor(Class<T> clazz, FriendlyByteBuf constArgs) {
        try {
            // find static field with class SyncConfigurer --Zeus
            var configurerField = Arrays.stream(clazz.getFields())
                    .filter(f -> f.getType().getSimpleName().equals("SyncConfigurer"))
                    .findFirst()
                    .orElse(null);
            if (configurerField == null) return null;
            if (!(configurerField.get(null) instanceof SyncConfigurer syncConfigurer)) return null;
            var orderedClasses = syncConfigurer.getRegisteredClasses();
            var decodedArgs = syncConfigurer.decodeAll(constArgs);
            return clazz.getConstructor(orderedClasses).newInstance(decodedArgs);
        } catch (Exception ignored) {
            return null;
        }
    }
}
