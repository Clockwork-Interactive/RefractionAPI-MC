package net.refractionapi.refraction.feature.task;

import net.minecraft.server.level.ServerLevel;

import java.util.function.BiFunction;
import java.util.function.Consumer;

public class LevelTaskHolder<T extends LevelTask> extends TaskHolder<T, ServerLevel> {
    public LevelTaskHolder(String id, BiFunction<ServerLevel, Object[], T> taskFunc, TaskFactory<ServerLevel> factory, Consumer<LevelTaskHolder<T>> configure) {
        super(id, taskFunc, factory);
        configure.accept(this);
    }

    @Override
    public T addTask(T task) {
        LevelTasks.get(task.accessor).addTask(task);
        return task;
    }
}
