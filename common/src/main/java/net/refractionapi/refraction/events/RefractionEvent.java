package net.refractionapi.refraction.events;

/**
 * Was made for multiloader simplicity :P --Zeus
 */
public abstract class RefractionEvent<T> {

    protected T event;

    protected RefractionEvent() {
        
    }

    public abstract void register(T listener);

    public T invoker() {
        return event;
    }

}
