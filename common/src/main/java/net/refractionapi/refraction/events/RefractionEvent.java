package net.refractionapi.refraction.events;

/**
 * Was made for multiloader simplicity :P --Zeus
 */
public abstract class RefractionEvent<T> {
    protected T event;

    protected RefractionEvent() {
        
    }

    public abstract T register(T listener);

    public abstract void unregister(T listener);

    public T invoker() {
        return event;
    }
}
