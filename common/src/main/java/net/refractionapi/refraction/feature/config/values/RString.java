package net.refractionapi.refraction.feature.config.values;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.refractionapi.refraction.feature.config.ReconfigValue;

public class RString extends ReconfigValue<String> {
    public RString(String id, String comment, String original) {
        super(id, comment, original);
    }

    @Override
    public StreamCodec<ByteBuf, String> streamCodec() {
        return ByteBufCodecs.STRING_UTF8;
    }

    @Override
    public JsonElement element() {
        return new JsonPrimitive(get());
    }

    @Override
    public String fromElement(JsonElement element) {
        return element.getAsString();
    }
}
