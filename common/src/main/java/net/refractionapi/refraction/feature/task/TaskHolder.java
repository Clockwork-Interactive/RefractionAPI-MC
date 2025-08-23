package net.refractionapi.refraction.feature.task;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.refractionapi.refraction.Refraction;
import net.refractionapi.refraction.helper.clazz.RModRegistrar;

import java.util.function.BiFunction;

public abstract class TaskHolder<T extends Task<A>, A> {
    protected final ResourceLocation id;
    protected final BiFunction<A, Object[], T> taskFunc;
    protected final TaskFactory<A> deserializer;
    public int maxTicks = -1;
    public String taskDesc = "";
    public boolean created = false;

    public TaskHolder(String id, BiFunction<A, Object[], T> taskFunc, TaskFactory<A> factory) {
        this.id = ResourceLocation.fromNamespaceAndPath(RModRegistrar.getCallerModID(2), id);
        this.taskFunc = taskFunc;
        this.deserializer = factory;
        Tasks.registerTask(this.id, this);
    }

    public T create(A accessor, Object... args) {
        T task = null;
        try {
            task = addTask(taskFunc.apply(accessor, args));
        } catch (Exception e) {
            Refraction.LOGGER.error("Couldn't create Task", e);
        }
        return task;
    }

    @SuppressWarnings("unchecked")
    public T fromNBT(A serverLevel, TaskHolder<?, ?> holder, ResourceLocation location, CompoundTag tag) {
        T task = null;
        try {
            task = addTask((T) deserializer.create(serverLevel, holder, location, tag));
        } catch (Exception e) {
            Refraction.LOGGER.error("Couldn't create Task", e);
        }
        return task;
    }

    public abstract T addTask(T task);

    public TaskHolder<T, A> setMaxTicks(int ticks) {
        this.maxTicks = ticks;
        return this;
    }

    public TaskHolder<T, A> setDesc(String desc) {
        this.taskDesc = desc;
        return this;
    }

    public boolean allowOnlyOne() {
        return false;
    }

    public ResourceLocation id() {
        return id;
    }

    @FunctionalInterface
    public interface TaskFactory<A> {
        Task create(A accessor, TaskHolder<?, ?> holder, ResourceLocation location, CompoundTag tag);
    }
}
