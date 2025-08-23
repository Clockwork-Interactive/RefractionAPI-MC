package net.refractionapi.refraction.feature.task;

import net.minecraft.server.level.ServerPlayer;

import java.util.function.BiFunction;
import java.util.function.Consumer;

public class PlayerTaskHolder<T extends PlayerTask> extends TaskHolder<T, ServerPlayer> {
    public PlayerTaskHolder(String id, BiFunction<ServerPlayer, Object[], T> taskFunc, TaskFactory<ServerPlayer> factory, Consumer<PlayerTaskHolder<T>> configure) {
        super(id, taskFunc, factory);
        configure.accept(this);
    }

    @Override
    public T addTask(T task) {
        PlayerTasks.get(task.accessor).addTask(task);
        return task;
    }
}
