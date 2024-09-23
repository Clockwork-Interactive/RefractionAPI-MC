package net.refractionapi.refraction.events;

import java.util.function.Function;

import java.lang.reflect.Array;

public class RefractionEventCaller<T> extends RefractionEvent<T> {

    private final Function<T[], T> invoker;
    private T[] listeners;
    private final Class<T> type;

    public RefractionEventCaller(Class<T> type, Function<T[], T> invoker) {
        this.type = type;
        this.invoker = invoker;
        this.listeners = (T[]) Array.newInstance(type, 0);
        this.update();
    }

    @Override
    public void register(T listener) {
        T[] newListeners = (T[]) Array.newInstance(type, listeners.length + 1);
        System.arraycopy(listeners, 0, newListeners, 0, listeners.length);
        newListeners[listeners.length] = listener;
        listeners = newListeners;
        this.update();
    }

    public void update() {
        this.event = invoker.apply(listeners);
    }

}