package net.refractionapi.refraction.init;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.ServiceLoader;
import java.util.Set;
import java.util.stream.Collectors;

public abstract class Reservice {
    protected final HashMap<Class<?>, Set<Object>> serviceCache = new HashMap<>();

    public <V> Set<ServiceLoader.Provider<V>> getProviders(Class<V> clazz) {
        return ServiceLoader.load(clazz)
                .stream()
                .collect(Collectors.toSet());
    }

    public <V> Set<V> loadAll(Class<V> clazz) {
        return getProviders(clazz)
                .stream()
                .map(ServiceLoader.Provider::get)
                .collect(Collectors.toSet());
    }

    public void cache(Class<?> clazz, Set<Object> services) {
        serviceCache.put(clazz, services);
    }

    public <V> Set<V> getCached(Class<V> clazz) {
        Set<Object> cached = serviceCache.get(clazz);
        if (cached == null) return Collections.emptySet();
        return cached.stream()
                .map(clazz::cast)
                .collect(Collectors.toSet());
    }

    public <V> Set<V> getAndDumpCache(Class<V> clazz) {
        Set<V> cached = getCached(clazz);
        dumpCache(clazz);
        return cached;
    }

    public void dumpCache(Class<?> clazz) {
        serviceCache.remove(clazz);
    }

    public <V> Set<V> loadAndCache(Class<V> clazz) {
        Set<V> services = loadAll(clazz);
        cache(clazz, new HashSet<>(services));
        return services;
    }

    public abstract void setupServices();

    public abstract void closeServices();
}
