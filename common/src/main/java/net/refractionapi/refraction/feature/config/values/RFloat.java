package net.refractionapi.refraction.feature.config.values;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.refractionapi.refraction.feature.config.ReconfigValue;

public class RFloat extends ReconfigValue<Float> {
    public RFloat(String id, String comment, Float original) {
        super(id, comment, original);
    }

    @Override
    public StreamCodec<ByteBuf, Float> streamCodec() {
        return ByteBufCodecs.FLOAT;
    }

    @Override
    public JsonElement element() {
        return new JsonPrimitive(get());
    }

    @Override
    public Float fromElement(JsonElement element) {
        return element.getAsFloat();
    }
}
