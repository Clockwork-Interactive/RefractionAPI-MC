package net.refractionapi.refraction.events;

import net.refractionapi.refraction.Refraction;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Function;

public class RefractionEventCaller<T> extends RefractionEvent<T> {
    private final Function<List<T>, T> invoker;
    private final CopyOnWriteArrayList<T> listeners;
    private final Class<T> type;

    public RefractionEventCaller(Class<T> type, Function<List<T>, T> invoker) {
        this.type = type;
        this.invoker = invoker;
        this.listeners = new CopyOnWriteArrayList<>();
        this.update();
    }

    @Override
    public T register(T listener) {
        if (!this.type.isInstance(listener)) {
            Refraction.LOGGER.error("Tried to register listener of type {} to event of type {}", listener.getClass(), this.type);
            return null;
        }
        this.listeners.add(listener);
        this.update();
        return listener;
    }

    @Override
    public void unregister(T listener) {
        this.listeners.remove(listener);
        this.update();
    }

    public void update() {
        this.event = invoker.apply(listeners);
    }
}