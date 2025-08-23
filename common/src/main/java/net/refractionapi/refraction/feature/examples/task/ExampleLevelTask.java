package net.refractionapi.refraction.feature.examples.task;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.refractionapi.refraction.feature.task.LevelTask;
import net.refractionapi.refraction.feature.task.TaskHolder;

public class ExampleLevelTask extends LevelTask {
    public ExampleLevelTask(ServerLevel level, Object... args) {
        super(ExampleTaskRegistry.EXAMPLE_LEVEL.id(), ExampleTaskRegistry.EXAMPLE_LEVEL, level);
    }

    public ExampleLevelTask(ServerLevel serverLevel, TaskHolder<?, ?> holder, ResourceLocation location, CompoundTag tag) {
        super(serverLevel, holder, location, tag);
    }

    public ExampleLevelTask() {
        super();
    }

    @Override
    public void onStart() {

    }

    @Override
    public void tick() {

    }
}
