package net.refractionapi.refraction.feature.reconfig;

import net.refractionapi.refraction.util.Mutable;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

public class RCBuilder {
    private Value[] values = new Value[0];
    private Value current = null;
    protected ReConfigurer.Side side;
    protected String file;
    protected String name;
    protected boolean syncOnSave = false;

    public RCBuilder() {
    }

    public RCBuilder set(String name, boolean defaultValue) {
        return define(name, "", new RCBoolean(defaultValue));
    }

    public RCBuilder set(String name, String description, boolean defaultValue) {
        return define(name, description, new RCBoolean(defaultValue));
    }

    public RCBuilder set(String name, double defaultValue) {
        return define(name, "", new RCDouble(defaultValue));
    }

    public RCBuilder set(String name, String description, double defaultValue) {
        return define(name, description, new RCDouble(defaultValue));
    }

    public RCBuilder set(String name, int defaultValue) {
        return define(name, "", new RCInteger(defaultValue));
    }

    public RCBuilder set(String name, String description, int defaultValue) {
        return define(name, description, new RCInteger(defaultValue));
    }

    public RCBuilder set(String name, String defaultValue) {
        return define(name, "", new RCString(defaultValue));
    }

    public RCBuilder set(String name, String description, String defaultValue) {
        return define(name, description, new RCString(defaultValue));
    }

    public <T extends ReconfigValue<?>> RCBuilder set(String name, Class<T> clazz, List<?> defaultValue) {
        return define(name, "", new RCList<>(clazz, defaultValue));
    }

    public <T extends ReconfigValue<?>> RCBuilder set(String name, String description, Class<T> clazz, List<?> defaultValue) {
        return define(name, description, new RCList<>(clazz, defaultValue));
    }

    public <K extends ReconfigValue<?>, V extends ReconfigValue<?>> RCBuilder set(String name, Class<K> keyClass, Class<V> valueClass, HashMap<K, V> defaultValue) {
        return define(name, "", new RCMap<>(keyClass, valueClass, defaultValue));
    }

    public <K extends ReconfigValue<?>, V extends ReconfigValue<?>> RCBuilder set(String name, String description, Class<K> keyClass, Class<V> valueClass, HashMap<K, V> defaultValue) {
        return define(name, description, new RCMap<>(keyClass, valueClass, defaultValue));
    }

    public <K extends ReconfigValue<?>, V extends ReconfigValue<?>> RCBuilder set(String name, Class<K> keyClass, Class<V> valueClass, Map<K, V> defaultValue) {
        return set(name, "", keyClass, valueClass, new HashMap<>(defaultValue));
    }

    public <K extends ReconfigValue<?>, V extends ReconfigValue<?>> RCBuilder set(String name, String description, Class<K> keyClass, Class<V> valueClass, Map<K, V> defaultValue) {
        return set(name, description, keyClass, valueClass, new HashMap<>(defaultValue));
    }

    public <T> RCBuilder check(Predicate<T> valid) {
        this.current.valid().set(valid);
        return this;
    }

    public <T extends ReconfigValue<?>> T build() {
        return (T) this.current.value;
    }

    protected RCBuilder with(ReConfigurer.Side side, String name, String file) {
        this.side = side;
        this.name = name;
        this.file = file;
        return this;
    }

    protected void save() {
        if (this.file == null) throw new IllegalStateException("Cannot save before config is registered!");
        ReConfigurer.save(this.file, this);
        ReConfigurer.syncCommonConfig(this);
    }

    protected void load() {
        if (this.file == null) throw new IllegalStateException("Cannot load before config is registered!");
        ReConfigurer.load(this.file, this);
    }

    protected <V, T extends ReconfigValue<V>> RCBuilder define(String name, String description, T value) {
        value.setBuilder(this);
        this.current = new Value<>(new Mutable<>(name), new Mutable<>(description), new Mutable<>((v) -> true), value);
        addValue(current);
        return this;
    }

    protected void addValue(Value<?, ?> value) {
        Value<?, ?>[] newValues = new Value[values.length + 1];
        System.arraycopy(values, 0, newValues, 0, values.length);
        newValues[values.length] = value;
        values = newValues;
    }

    protected Collection<Value<?, ?>> values() {
        return List.of(values);
    }

    protected boolean valueExists(String key) {
        return values().stream().anyMatch(value -> value.name().get().equals(key) || key.equals("comment-%s".formatted(value.name.get())));
    }

    public RCBuilder copy() {
        Value[] valuesCopy = this.values;
        Value currentCopy = this.current;
        ReConfigurer.Side sideCopy = this.side;
        String fileCopy = this.file;
        String nameCopy = this.name;
        return new RCBuilder() {{
            values = valuesCopy;
            current = currentCopy;
            side = sideCopy;
            file = fileCopy;
            name = nameCopy;
        }};
    }

    public record Value<V, T extends ReconfigValue<V>>(Mutable<String> name, Mutable<String> description,
                                                       Mutable<Predicate<V>> valid, T value) {
    }
}
