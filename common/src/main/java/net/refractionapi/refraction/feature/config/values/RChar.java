package net.refractionapi.refraction.feature.config.values;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.refractionapi.refraction.codec.ExtraBufCodecs;
import net.refractionapi.refraction.feature.config.ReconfigValue;

public class RChar extends ReconfigValue<Character> {
    public RChar(String id, String comment, Character original) {
        super(id, comment, original);
    }

    @Override
    public StreamCodec<ByteBuf, Character> streamCodec() {
        return ExtraBufCodecs.CHAR;
    }

    @Override
    public JsonElement element() {
        return new JsonPrimitive(get());
    }

    @Override
    public Character fromElement(JsonElement element) {
        return element.getAsCharacter();
    }
}
