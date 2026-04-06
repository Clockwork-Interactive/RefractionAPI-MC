package net.refractionapi.refraction.util;

import net.minecraft.resources.ResourceLocation;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public class Registry<K, V> {
    final ResourceLocation registryID;
    final Map<K, V> registry = new HashMap<>();
    final Map<Class<?>, Function<Object, K>> keyMappers = new HashMap<>();
    final Map<Class<?>, Function<Collection<V>, Collection<Object>>> valueMappers = new HashMap<>();
    V defaultReturn = null;

    public Registry(ResourceLocation registryID) {
        this.registryID = registryID;
    }

    public Registry<K, V> setDefaultReturn(V defaultReturn) {
        this.defaultReturn = defaultReturn;
        return this;
    }

    @SuppressWarnings("unchecked")
    public <M> Registry<K, V> keyMapper(Class<M> type, Function<M, K> mapper) {
        keyMappers.put(type, (Function<Object, K>) mapper);
        return this;
    }

    @SuppressWarnings("unchecked")
    public <M> Registry<K, V> valueMapper(Class<M> type, Function<Collection<V>, Collection<M>> mapper) {
        valueMappers.put(type, (Function<Collection<V>, Collection<Object>>) (Function<?, ?>) mapper);
        return this;
    }

    public ResourceLocation id() {
        return registryID;
    }

    public Collection<K> keys() {
        return registry.keySet();
    }

    public Collection<V> values() {
        return registry.values();
    }

    public boolean containsKey(K key) {
        return registry.containsKey(key);
    }

    public V register(K key, V value) {
        registry.put(key, value);
        return value;
    }

    public V get(K key) {
        return registry.getOrDefault(key, defaultReturn);
    }

    public V getMapped(Object key) {
        return get(keyMappers.get(key.getClass()).apply(key));
    }

    @SuppressWarnings("unchecked")
    public <M> Collection<M> getMappedValues(Class<M> type) {
        return (Collection<M>) valueMappers.get(type).apply(values());
    }
}
