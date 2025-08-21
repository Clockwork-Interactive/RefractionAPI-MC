package net.refractionapi.refraction.feature.examples.task;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.refractionapi.refraction.feature.task.Task;
import net.refractionapi.refraction.feature.task.TaskHolder;

public class ExampleTask extends Task {
    public ExampleTask(ServerLevel level, Object... args) {
        super(ExampleTaskRegistry.EXAMPLE.id(), ExampleTaskRegistry.EXAMPLE, level);
    }

    public ExampleTask(ServerLevel serverLevel, TaskHolder<?> holder, ResourceLocation location, CompoundTag tag) {
        super(serverLevel, holder, location, tag);
    }

    @Override
    public void onStart() {

    }

    @Override
    public void tick() {

    }
}
