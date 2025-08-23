package net.refractionapi.refraction.feature.task;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;

public abstract class LevelTask extends Task<ServerLevel> {
    public LevelTask(ResourceLocation id, TaskHolder<?, ?> holder, ServerLevel accessor) {
        super(id, holder, accessor);
    }

    public LevelTask(ServerLevel serverLevel, TaskHolder<?, ?> holder, ResourceLocation id, CompoundTag tag) {
        super(serverLevel, holder, id, tag);
    }

    public LevelTask() {
        super();
    }


    @Override
    public Level level() {
        return null;
    }
}
