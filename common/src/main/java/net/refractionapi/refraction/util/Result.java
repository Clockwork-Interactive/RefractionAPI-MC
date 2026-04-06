package net.refractionapi.refraction.util;

import net.minecraft.nbt.CompoundTag;

public class Result<V> {
    private final V value;
    private final String error;

    private Result(V value, String error) {
        this.value = value;
        this.error = error;
    }

    public static <V> Result<V> ok(V value) {
        return new Result<>(value, null);
    }

    public static <V> Result<V> error(String error) {
        return new Result<>(null, error);
    }

    public boolean isOk() {
        return error == null;
    }

    public boolean isError() {
        return !isOk();
    }

    public V value() {
        return value;
    }

    public String errorMsg() {
        return error;
    }

    public CompoundTag toTag() {
        CompoundTag tag = new CompoundTag();
        if (isOk()) {
            tag.putBoolean("ok", true);
            tag.putString("value", value.toString());
        } else {
            tag.putBoolean("ok", false);
            tag.putString("error", error);
        }
        return tag;
    }

    public static <V> Result<V> fromTag(CompoundTag tag, ValueParser<V> parser) {
        boolean ok = tag.getBoolean("ok");
        if (ok) {
            V value = parser.parse(tag.getString("value"));
            return Result.ok(value);
        } else {
            String error = tag.getString("error");
            return Result.error(error);
        }
    }

    @FunctionalInterface
    public interface ValueParser<V> {
        V parse(String value);
    }

    public class Parsers {
        public static ValueParser<Integer> INTEGER = Integer::parseInt;
        public static ValueParser<Float> FLOAT = Float::parseFloat;
        public static ValueParser<String> STRING = value -> value;
    }
}
