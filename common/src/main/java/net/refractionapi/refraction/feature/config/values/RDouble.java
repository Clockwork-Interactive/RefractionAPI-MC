package net.refractionapi.refraction.feature.config.values;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.refractionapi.refraction.feature.config.ReconfigValue;

public class RDouble extends ReconfigValue<Double> {
    public RDouble(String id, String comment, Double original) {
        super(id, comment, original);
    }

    @Override
    public StreamCodec<ByteBuf, Double> streamCodec() {
        return ByteBufCodecs.DOUBLE;
    }

    @Override
    public JsonElement element() {
        return new JsonPrimitive(get());
    }

    @Override
    public Double fromElement(JsonElement element) {
        return element.getAsDouble();
    }
}
