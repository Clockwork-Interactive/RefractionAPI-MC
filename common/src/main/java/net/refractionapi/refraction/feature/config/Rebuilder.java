package net.refractionapi.refraction.feature.config;

import net.refractionapi.refraction.feature.config.values.*;

import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

public class Rebuilder {
    private static final HashMap<Class<?>, ReconfigValue.AbstractFactory<?>> factories = new HashMap<>();
    final LinkedHashMap<String, ReconfigValue<?>> values = new LinkedHashMap<>();
    String currentCategory = "";
    String currentComment = "";

    protected Rebuilder() {
    }

    public Rebuilder push(String category) {
        this.currentCategory = category;
        return this;
    }

    public Rebuilder pop() {
        this.currentCategory = "";
        return this;
    }

    public Rebuilder comment(String comment) {
        this.currentComment = comment;
        return this;
    }

    private void popAll() {
        this.currentCategory = "";
    }

    @SuppressWarnings("unchecked")
    public <V, T extends ReconfigValue<V>> T define(String id, V obj) {
        var val = fromRaw(obj, id, currentComment);
        popAll();
        if (val == null) throw new IllegalArgumentException("No factory for type " + obj.getClass());
        put(id, val);
        return (T) val;
    }

    public <T> ReconfigValue<T> define(String id, ReconfigValue<T> value) {
        popAll();
        put(id, value);
        return value;
    }

    private void put(String id, ReconfigValue<?> value) {
        if (values.containsKey(id)) throw new IllegalArgumentException("Duplicate config id: " + id);
        values.put(id, value);
    }

    @SuppressWarnings("unchecked")
    public static <T> ReconfigValue<T> fromRaw(T raw, String id, String comment) {
        return factories.containsKey(raw.getClass()) ?
                ((ReconfigValue.AbstractFactory<T>) factories.get(raw.getClass())).create(id, comment, raw) : null;
    }

    public void save() {

    }

    private static <T> void register(Class<T> clazz, ReconfigValue.Factory<T> factory) {
        factories.put(clazz, factory);
    }

    private static <V> void register(Class<V> clazz, ReconfigValue.ClassFactory<V> factory) {
        factories.put(clazz, factory);
    }

    @Override
    public String toString() {
        return "Rebuilder[" +
                "values=" + Arrays.toString(values.values().stream().map(Object::toString).toArray()) +
                ']';
    }

    static {
        register(String.class, RString::new);
        register(Boolean.class, RBool::new);
        register(boolean.class, RBool::new);
        register(Integer.class, RInt::new);
        register(int.class, RInt::new);
        register(Float.class, RFloat::new);
        register(float.class, RFloat::new);
        register(Double.class, RDouble::new);
        register(double.class, RDouble::new);
        register(Long.class, RLong::new);
        register(long.class, RLong::new);
    }
}
