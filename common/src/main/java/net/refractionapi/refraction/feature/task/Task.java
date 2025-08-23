package net.refractionapi.refraction.feature.task;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.refractionapi.refraction.feature.data.Syncable;

import java.util.UUID;

public abstract class Task<A> implements Syncable<Task<A>> {
    protected int tickCount = 0;
    protected final TaskHolder<?, ?> holder;
    protected final ResourceLocation id;
    protected UUID uuid;
    protected final A accessor;
    protected State state = State.RUNNING;
    protected boolean removed = false;
    protected CompoundTag loadingTag = new CompoundTag();

    public Task(ResourceLocation id, TaskHolder<?, ?> holder, A accessor) {
        this.holder = holder;
        this.accessor = accessor;
        this.id = id;
        this.setSynced();
    }

    public Task(A accessor, TaskHolder<?, ?> holder, ResourceLocation id, CompoundTag tag) {
        this.accessor = accessor;
        this.holder = holder;
        this.id = id;
        this.loadingTag = tag;
        this.setSynced();
    }

    public Task() {
        this.holder = null;
        this.id = null;
        this.accessor = null;
    }

    public String maxTickString() {
        return maxTicks() == -1 ? "inf" : maxTicks() + "";
    }

    public State state() {
        return state;
    }

    public int maxTicks() {
        return holder.maxTicks;
    }

    public final void postAdd() {
        if (loadingTag.isEmpty()) return;
        this.load(loadingTag);
    }

    public void onAdd() {

    }

    public abstract void onStart();

    public abstract void tick();

    public void onEnd() {

    }

    public void end() {
        state = State.STOPPED;
    }

    public void handleStateTick() {

    }

    public abstract Level level();

    public void save(CompoundTag tag) {
        tag.putInt("ticks", tickCount);
        tag.putString("state", state.name());
        tag.putUUID("uuid", uuid = uuid == null ? UUID.randomUUID() : uuid);
    }

    public void load(CompoundTag tag) {
        this.tickCount = tag.getInt("ticks");
        this.state = State.valueOf(tag.getString("state"));
        this.uuid = tag.getUUID("uuid");
    }

    @Override
    public void write(FriendlyByteBuf buf) {
        CompoundTag tag = new CompoundTag();
        save(tag);
        buf.writeNbt(tag);
    }

    @Override
    public void read(FriendlyByteBuf buf) {
        CompoundTag tag = buf.readNbt();
        if (tag == null) return;
        load(tag);
    }

    public int ticks() {
        return tickCount;
    }

    public ResourceLocation id() {
        return id;
    }

    public TaskHolder<?, ?> holder() {
        return holder;
    }

    public enum State {
        RUNNING,
        PAUSED,
        STOPPED
    }
}
