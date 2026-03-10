package net.refractionapi.refraction.feature.config;

import lombok.Getter;
import net.minecraft.network.FriendlyByteBuf;
import net.refractionapi.refraction.util.Side;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.function.Consumer;

/**
 * Holder class for information about the configuration set.
 * --Zeus
 */
public class Reconfig {
    final Rebuilder builder;
    @Getter
    private boolean registered = false;
    Side side;
    String fileName;

    public Reconfig(Rebuilder builder) {
        this.builder = builder;
    }

    protected boolean addToManager(String fileName, Side side) {
        this.side = side;
        this.fileName = fileName;
        return !registered && (registered = true);
    }

    public boolean isServer() {
        return side == Side.SERVER;
    }

    public boolean valueExists(String name) {
        return builder.values.containsKey(name);
    }

    protected LinkedHashMap<String, ReconfigValue<?>> valueMap() {
        return builder.values;
    }

    protected Collection<ReconfigValue<?>> values() {
        return builder.values.values();
    }

    protected void decode(FriendlyByteBuf buf) {
        for (ReconfigValue<?> value : builder.values.values()) {
            value.decodeValue(buf);
        }
    }

    @SuppressWarnings("unchecked")
    public <T> ReconfigValue<T> get(String id) {
        return (ReconfigValue<T>) builder.values.get(id);
    }

    // DO NOT USE THIS FOR NOW! --Zeus
    public static Reconfig create(Consumer<Rebuilder> builder) {
        Rebuilder rebuilder = new Rebuilder();
        builder.accept(rebuilder);
        return new Reconfig(rebuilder);
    }
}
