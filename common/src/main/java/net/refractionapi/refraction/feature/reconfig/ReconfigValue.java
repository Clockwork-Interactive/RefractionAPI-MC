package net.refractionapi.refraction.feature.reconfig;

import com.google.gson.JsonObject;
import net.refractionapi.refraction.Refraction;

import java.util.Objects;

public abstract class ReconfigValue<T> {
    protected T value;
    protected RCBuilder builder;

    public ReconfigValue(T defaultValue) {
        this.value = defaultValue;
    }

    public ReconfigValue() {
        this.value = null;
    }

    protected T getValue() {
        return value;
    }

    public T get() {
        if (this.builder == null)
            throw new IllegalStateException("Cannot get value on invalid dist or before config is registered!");
        return this.value;
    }

    public <V extends ReconfigValue<?>> V create(Class<V> configClass) {
        try {
            return configClass.getConstructor().newInstance();
        } catch (Exception e) {
            Refraction.LOGGER.error("Failed to create ReconfigValue instance", e);
            return null;
        }
    }

    /**
     * Expensively loads the value from the config file
     */
    public T loadAndGet() {
        if (this.builder == null)
            throw new IllegalStateException("Cannot get value on invalid dist or before config is registered!");
        this.builder.load();
        return this.value;
    }

    protected void setBuilder(RCBuilder builder) {
        this.builder = builder;
    }

    public void set(T value) {
        if (this.builder == null) throw new IllegalStateException("Cannot set value before config is registered!");
        this.value = value;
        this.builder.save();
    }

    public abstract void serialize(String name, JsonObject object);

    public abstract void deserialize(String name, JsonObject object);

    public abstract String type();

    @Override
    public boolean equals(Object obj) {
        return obj instanceof ReconfigValue<?> value && value == this.value;
    }

    @Override
    public int hashCode() {
        return Objects.hash(value, this.type());
    }
}
