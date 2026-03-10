package net.refractionapi.refraction.feature.config.values;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.refractionapi.refraction.feature.config.ReconfigValue;

public class RLong extends ReconfigValue<Long> {
    public RLong(String id, String comment, Long original) {
        super(id, comment, original);
    }

    @Override
    public StreamCodec<ByteBuf, Long> streamCodec() {
        return ByteBufCodecs.VAR_LONG;
    }

    @Override
    public JsonElement element() {
        return new JsonPrimitive(get());
    }

    @Override
    public Long fromElement(JsonElement element) {
        return element.getAsLong();
    }
}
