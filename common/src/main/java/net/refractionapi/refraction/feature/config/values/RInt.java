package net.refractionapi.refraction.feature.config.values;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.refractionapi.refraction.feature.config.ReconfigValue;

public class RInt extends ReconfigValue<Integer> {
    public RInt(String id, String comment, Integer original) {
        super(id, comment, original);
    }

    @Override
    public StreamCodec<ByteBuf, Integer> streamCodec() {
        return ByteBufCodecs.INT;
    }

    @Override
    public JsonElement element() {
        return new JsonPrimitive(get());
    }

    @Override
    public Integer fromElement(JsonElement element) {
        return element.getAsInt();
    }
}
