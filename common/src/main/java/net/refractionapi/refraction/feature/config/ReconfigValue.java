package net.refractionapi.refraction.feature.config;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import io.netty.buffer.ByteBuf;
import lombok.Getter;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

import java.util.List;
import java.util.function.Predicate;

public abstract class ReconfigValue<T> {
    protected Rebuilder builder = null;
    private Predicate<T> validator = v -> true;
    private final ThreadLocal<T> value = new ThreadLocal<>();
    @Getter
    final String id;
    final T original;
    final String comment;

    public ReconfigValue(String id, String comment, T original) {
        this.id = id;
        this.comment = comment;
        this.original = original;
        set(original);
    }

    public abstract StreamCodec<ByteBuf, T> streamCodec();

    public abstract JsonElement element();

    public abstract T fromElement(JsonElement element);

    public boolean registered() {
        return builder != null;
    }

    public void set(T value) {
        if (!validator.test(value)) throw new IllegalArgumentException("Invalid value for config " + id + ": " + value);
        this.value.set(value);
        if (registered()) builder.save();
    }

    public T get() {
        return value.get();
    }

    public ReconfigValue<T> validator(Predicate<T> validator) {
        this.validator = validator;
        return this;
    }

    public String commentID() {
        return id + "_comment";
    }

    public void encodeValue(ByteBuf buf) {
        streamCodec().encode(buf, get());
    }

    public void decodeValue(ByteBuf buf) {
        set(streamCodec().decode(buf));
    }

    public void write(JsonObject obj) {
        obj.add(id, element());
    }

    public void read(JsonObject obj) {
        if (obj.has(id)) fromElement(obj.get(id));
    }

    @Override
    public String toString() {
        return get().toString();
    }

    @FunctionalInterface
    public interface Factory<T> extends AbstractFactory<T> {
        ReconfigValue<T> create(String id, String comment, T original);

        @SuppressWarnings("unchecked")
        @Override
        default ReconfigValue<T> create(Object... args) {
            return create((String) args[0], (String) args[1], (T) args[2]);
        }
    }

    @FunctionalInterface
    public interface ClassFactory<T> extends AbstractFactory<T> {
        ReconfigValue<T> create(Class<T> clazz, String id, String comment, T original);

        @SuppressWarnings("unchecked")
        @Override
        default ReconfigValue<T> create(Object... args) {
            return create((Class<T>) args[0], (String) args[1], (String) args[2], (T) args[3]);
        }
    }

    public interface AbstractFactory<T> {
        default ReconfigValue<T> create(Object... args) {
            throw new UnsupportedOperationException("Not implemented");
        }
    }
}
