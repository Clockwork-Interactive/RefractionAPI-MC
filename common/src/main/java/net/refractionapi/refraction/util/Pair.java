package net.refractionapi.refraction.util;

public class Pair<F, S> {
    public F first;
    public S second;

    public Pair(F first, S second) {
        this.first = first;
        this.second = second;
    }

    public F getFirst() {
        return first;
    }

    public S getSecond() {
        return second;
    }

    public void setFirst(F first) {
        this.first = first;
    }

    public void setSecond(S second) {
        this.second = second;
    }

    public Pair<F, S> copy() {
        return new Pair<>(this.first, this.second);
    }

    public static <G, H> Pair<G, H> of(G first, H second) {
        return new Pair<>(first, second);
    }

    public boolean equals(Object obj) {
        if (obj instanceof Pair<?, ?> pair) {
            return pair.first.equals(first) && pair.second.equals(second);
        }
        return false;
    }
}
