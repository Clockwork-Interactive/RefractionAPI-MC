package net.refractionapi.refraction.feature.task;

import net.minecraft.server.level.ServerLevel;
import net.refractionapi.refraction.mixininterfaces.ILevel;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Minimalist task persistent handling
 */
public class LevelTasks extends Tasks<ServerLevel, LevelTask> {
    private final List<LevelTask> tasks = new CopyOnWriteArrayList<>();

    public LevelTasks(ServerLevel level) {
        super(level);
    }

    @Override
    public List<LevelTask> tasks() {
        return tasks;
    }

    @Override
    public ServerLevel level() {
        return accessor;
    }

    public static LevelTasks get(ServerLevel level) {
        return level instanceof ILevel iLevel ? iLevel.tasks() : null;
    }
}
