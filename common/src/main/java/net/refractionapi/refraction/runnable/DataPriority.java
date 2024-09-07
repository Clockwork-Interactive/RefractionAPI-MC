package net.refractionapi.refraction.runnable;

import com.mojang.datafixers.util.Pair;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Predicate;

public class DataPriority<T> {

    private final HashMap<Integer, Pair<Predicate<?>, T>> data = new HashMap<>();

    public DataPriority() {

    }

    public <P> DataPriority<T> addData(int priority, Predicate<P> condition, T data) {
        if (priority < 0) throw new IllegalArgumentException("Priority must be greater than or equal to 0");
        this.data.put(priority, Pair.of(condition, data));
        return this;
    }

    public <P> DataPriority<T> addData(Predicate<P> condition, T data) {
        this.data.put(this.data.size(), Pair.of(condition, data));
        return this;
    }

    public DataPriority<T> addDefaultData(T data) {
        this.data.put(-1, Pair.of(t -> true, data));
        return this;
    }

    public void removeData(int priority) {
        this.data.remove(priority);
    }

    @SuppressWarnings("unchecked")
    public <P> Optional<T> getHighest(P test) {
        return this.data.entrySet().stream()
                .filter(entry -> ((Predicate<P>) entry.getValue().getFirst()).test(test))
                .min(Comparator.comparingInt(Map.Entry::getKey))
                .map(entry -> entry.getValue().getSecond()).or(() -> Optional.ofNullable(this.data.get(-1)).map(Pair::getSecond));
    }

    @SuppressWarnings("unchecked")
    public <P> Optional<T> getLowest(P test) {
        return this.data.entrySet().stream()
                .filter(entry -> ((Predicate<P>) entry.getValue().getFirst()).test(test))
                .max(Comparator.comparingInt(Map.Entry::getKey))
                .map(entry -> entry.getValue().getSecond()).or(() -> Optional.ofNullable(this.data.get(-1)).map(Pair::getSecond));
    }

}
