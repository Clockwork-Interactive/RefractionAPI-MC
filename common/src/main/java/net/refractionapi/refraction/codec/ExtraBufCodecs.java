package net.refractionapi.refraction.codec;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.refractionapi.refraction.feature.config.ReconfigValue;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Function;

public class ExtraBufCodecs {
    public static final StreamCodec<ByteBuf, Character> CHAR = create(
            ByteBuf::readChar,
            (buf, character) -> {
                buf.writeChar(character);
            }
    );

    public static <T> StreamCodec<ByteBuf, List<T>> listCodec(
            StreamCodec<ByteBuf, T> elementCodec
    ) {
        return create(
                byteBuf -> {
                    int size = byteBuf.readInt();
                    List<T> list = new ArrayList<>(size);
                    for (int i = 0; i < size; i++) list.add(elementCodec.decode(byteBuf));
                    return list;
                },
                (byteBuf, list) -> {
                    byteBuf.writeInt(list.size());
                    for (T element : list) elementCodec.encode(byteBuf, element);
                }
        );
    }

    public static <T> StreamCodec<ByteBuf, T> create(
            Function<ByteBuf, T> decoder,
            BiConsumer<ByteBuf, T> encoder
    ) {
        return new StreamCodec<>() {
            @Override
            public void encode(@NotNull ByteBuf byteBuf, @NotNull T t) {
                encoder.accept(byteBuf, t);
            }

            @Override
            public @NotNull T decode(@NotNull ByteBuf byteBuf) {
                return decoder.apply(byteBuf);
            }
        };
    }
}
