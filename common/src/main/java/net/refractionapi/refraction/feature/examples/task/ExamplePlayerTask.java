package net.refractionapi.refraction.feature.examples.task;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.refractionapi.refraction.Refraction;
import net.refractionapi.refraction.feature.task.PlayerTask;
import net.refractionapi.refraction.feature.task.TaskHolder;

public class ExamplePlayerTask extends PlayerTask {
    public ExamplePlayerTask(ServerPlayer player, Object... args) {
        super(ExampleTaskRegistry.EXAMPLE_PLAYER.id(), ExampleTaskRegistry.EXAMPLE_PLAYER, player);
    }

    public ExamplePlayerTask(ServerPlayer player, TaskHolder<?, ?> holder, ResourceLocation location, CompoundTag tag) {
        super(player, holder, location, tag);
    }

    public ExamplePlayerTask() {
        super();
    }

    @Override
    public void onAdd() {
        super.onAdd();
    }

    @Override
    public void save(CompoundTag tag) {
        super.save(tag);
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
    }

    @Override
    public void onStart() {

    }

    @Override
    public void tick() {

    }
}
