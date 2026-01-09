package net.refractionapi.refraction.events;

/**
 * Was made for multiloader simplicity :P --Zeus
 */
public abstract class RefractionEvent<T> {
    protected T event;

    protected RefractionEvent() {
        
    }

    public T register(T listener) {
        return register(Priority.NORMAL, listener);
    }

    public abstract T register(Priority priority, T listener);

    public abstract void unregister(T listener);

    public T invoker() {
        return event;
    }

    public enum Priority {
        LOWEST,
        LOW,
        NORMAL,
        HIGH,
        HIGHEST
    }
}
