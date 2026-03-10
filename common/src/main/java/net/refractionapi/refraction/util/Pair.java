package net.refractionapi.refraction.util;

import lombok.Getter;
import lombok.Setter;

import java.util.Objects;

public class Pair<F, S> {
    @Setter
    @Getter
    public F first;
    @Setter
    @Getter
    public S second;

    public Pair(F first, S second) {
        this.first = first;
        this.second = second;
    }

    public Pair<F, S> copy() {
        return new Pair<>(this.first, this.second);
    }

    public static <G, H> Pair<G, H> of(G first, H second) {
        return new Pair<>(first, second);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Pair<?, ?> pair = (Pair<?, ?>) obj;
        return Objects.equals(first, pair.first) &&
                Objects.equals(second, pair.second);
    }
}
