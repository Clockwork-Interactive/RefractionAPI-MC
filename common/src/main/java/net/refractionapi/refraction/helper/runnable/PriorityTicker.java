package net.refractionapi.refraction.helper.runnable;

import com.mojang.datafixers.util.Pair;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Predicate;

public class PriorityTicker<T> {

    private final HashMap<Integer, Pair<Predicate<T>, Consumer<T>>> tasks = new HashMap<>();
    private boolean noTask = false;

    public PriorityTicker() {

    }

    public PriorityTicker<T> addTask(int priority, Predicate<T> condition, Consumer<T> task) {
        if (priority < 0) throw new IllegalArgumentException("Priority must be greater than or equal to 0");
        this.tasks.put(priority, Pair.of(condition, task));
        return this;
    }

    public PriorityTicker<T> onNoTask(Consumer<T> task) {
        this.tasks.put(-1, Pair.of(t -> false, task));
        return this;
    }

    public PriorityTicker<T> sideTask(Consumer<T> task) {
        this.tasks.put(-2, Pair.of(t -> false, task));
        return this;
    }

    public PriorityTicker<T> idleTask(Consumer<T> task) {
        this.tasks.put(-3, Pair.of(t -> false, task));
        return this;
    }

    public void removeTask(int priority) {
        this.tasks.remove(priority);
    }

    public void tick(T t) {
        this.tasks.entrySet().stream()
                .filter(entry -> entry.getValue().getFirst().test(t))
                .min(Comparator.comparingInt(Map.Entry::getKey))
                .map(Map.Entry::getValue)
                .ifPresentOrElse(pair -> {
                    pair.getSecond().accept(t);
                    this.noTask = false;
                }, () -> {
                    if (!this.noTask) {
                        this.noTask = true;
                        Optional.ofNullable(this.tasks.get(-1)).ifPresent(pair -> pair.getSecond().accept(t));
                    }
                    Optional.ofNullable(this.tasks.get(-3)).ifPresent(pair -> pair.getSecond().accept(t));
                });
        Optional.ofNullable(this.tasks.get(-2)).ifPresent(pair -> pair.getSecond().accept(t));
    }

}
