package net.refractionapi.refraction.feature.task;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;

import java.util.UUID;

public abstract class Task {
    protected int tickCount = 0;
    protected final TaskHolder<?> holder;
    protected final ResourceLocation id;
    protected UUID uuid;
    protected final ServerLevel level;
    protected State state = State.RUNNING;

    public Task(ResourceLocation id, TaskHolder<?> holder, ServerLevel level) {
        this.holder = holder;
        this.level = level;
        this.id = id;
    }

    public Task(ServerLevel serverLevel, TaskHolder<?> holder, ResourceLocation id, CompoundTag tag) {
        this.level = serverLevel;
        this.holder = holder;
        this.id = id;
        this.deserialize(tag);
    }

    public String maxTickString() {
        return holder.maxTicks == -1 ? "inf" : holder.maxTicks + "";
    }

    public int maxTicks() {
        return holder.maxTicks;
    }

    public void onAdd() {

    }

    public abstract void onStart();

    public abstract void tick();

    public void onEnd() {

    }

    public void handleStateTick() {

    }

    public void serialize(CompoundTag tag) {
        tag.putInt("ticks", tickCount);
        tag.putUUID("uuid", uuid = uuid == null ? UUID.randomUUID() : uuid);
    }

    public void deserialize(CompoundTag tag) {
        this.tickCount = tag.getInt("ticks");
        this.uuid = tag.getUUID("uuid");
    }

    public int ticks() {
        return tickCount;
    }

    public ResourceLocation id() {
        return id;
    }

    public TaskHolder<?> holder() {
        return holder;
    }

    public enum State {
        RUNNING,
        PAUSED,
        STOPPED
    }
}
