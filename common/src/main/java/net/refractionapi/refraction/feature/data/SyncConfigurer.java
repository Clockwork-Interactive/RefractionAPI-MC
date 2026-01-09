package net.refractionapi.refraction.feature.data;

import net.minecraft.network.FriendlyByteBuf;

import java.util.LinkedHashMap;
import java.util.function.Function;

public class SyncConfigurer {
    private final LinkedHashMap<Class<?>, Function<FriendlyByteBuf, Object>> decoders = new LinkedHashMap<>();

    @SuppressWarnings("unchecked")
    public <T> SyncConfigurer registerDecoder(Class<T> clazz, Function<FriendlyByteBuf, T> decoder) {
        decoders.put(clazz, (Function<FriendlyByteBuf, Object>) decoder);
        return this;
    }

    public Class<?>[] getRegisteredClasses() {
        return decoders.keySet().toArray(new Class<?>[0]);
    }

    public Object decode(Class<?> clazz, FriendlyByteBuf buf) {
        var decoder = decoders.get(clazz);
        if (decoder == null) throw new IllegalArgumentException("No decoder registered for class: " + clazz.getName());
        return decoder.apply(buf);
    }

    public Object[] decodeAll(FriendlyByteBuf buf) {
        Object[] result = new Object[decoders.size()];
        int index = 0;
        for (Function<FriendlyByteBuf, Object> decoder : decoders.values()) {
            result[index++] = decoder.apply(buf);
        }
        return result;
    }
}
