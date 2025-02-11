package net.refractionapi.refraction.util;

public class Mutable<T> {
    public T value;

    public Mutable(T value) {
        this.value = value;
    }

    public T get() {
        return this.value;
    }

    public void set(T value) {
        this.value = value;
    }

    public String toString() {
        return this.value.toString();
    }

    public boolean equals(Object obj) {
        return obj instanceof Mutable<?> mutable && this.value.equals(mutable.value);
    }
}
