package net.refractionapi.refraction.feature.task;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.refractionapi.refraction.Refraction;
import net.refractionapi.refraction.helper.clazz.RModRegistrar;

import java.util.function.BiFunction;

public class TaskHolder<T extends Task> {
    protected final ResourceLocation id;
    protected final BiFunction<ServerLevel, Object[], T> taskFunc;
    protected final TaskFactory deserializer;
    public int maxTicks = -1;
    public String taskDesc = "";
    public boolean created = false;

    public TaskHolder(String id, BiFunction<ServerLevel, Object[], T> taskFunc, TaskFactory factory) {
        this.id = ResourceLocation.fromNamespaceAndPath(RModRegistrar.getCallerModID(2), id);
        this.taskFunc = taskFunc;
        this.deserializer = factory;
        Tasks.registerTask(this.id, this);
    }

    public T create(ServerLevel serverLevel, Object... args) {
        T task = null;
        try {
            task = addTask(taskFunc.apply(serverLevel, args));
        } catch (Exception e) {
            Refraction.LOGGER.error("Couldn't create Task", e);
        }
        return task;
    }

    @SuppressWarnings("unchecked")
    public T fromNBT(ServerLevel serverLevel, TaskHolder<?> holder, ResourceLocation location, CompoundTag tag) {
         T task = null;
         try {
             task = addTask((T) deserializer.create(serverLevel, holder, location, tag));
         } catch (Exception e) {
             Refraction.LOGGER.error("Couldn't create Task", e);
         }
         return task;
    }

    public T addTask(T task) {
        Tasks.get(task.level).addTask(task);
        return task;
    }

    public TaskHolder<T> setMaxTicks(int ticks) {
        this.maxTicks = ticks;
        return this;
    }

    public TaskHolder<T> setDesc(String desc) {
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
    public interface TaskFactory {
        Task create(ServerLevel serverLevel, TaskHolder<?> holder, ResourceLocation location, CompoundTag tag);
    }
}
