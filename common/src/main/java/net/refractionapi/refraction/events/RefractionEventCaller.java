package net.refractionapi.refraction.events;

import net.refractionapi.refraction.Refraction;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Function;

public class RefractionEventCaller<T> extends RefractionEvent<T> {
    private final Function<List<T>, T> invoker;
    private final ConcurrentHashMap<Priority, CopyOnWriteArrayList<T>> listeners;
    private final Class<T> type;

    public RefractionEventCaller(Class<T> type, Function<List<T>, T> invoker) {
        this.type = type;
        this.invoker = invoker;
        this.listeners = new ConcurrentHashMap<>();
        this.update();
    }

    @Override
    public T register(Priority priority, T listener) {
        if (!this.type.isInstance(listener)) {
            Refraction.LOGGER.error("Tried to register listener of type {} to event of type {}", listener.getClass(), this.type);
            return null;
        }
        this.listeners.computeIfAbsent(priority, k -> new CopyOnWriteArrayList<>()).add(listener);
        this.update();
        return listener;
    }

    @Override
    public void unregister(T listener) {
        this.listeners.forEach((priority, list) -> list.remove(listener));
        this.update();
    }

    public void update() {
        List<T> sortedListeners = this.listeners.keySet().stream()
            .sorted((a, b) -> Integer.compare(b.ordinal(), a.ordinal()))
            .flatMap(priority -> this.listeners.get(priority).stream())
            .toList();
        this.event = invoker.apply(sortedListeners);
    }
}