package net.refractionapi.refraction.feature.task;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.storage.LevelResource;
import net.refractionapi.refraction.Refraction;
import net.refractionapi.refraction.helper.misc.TagIO;
import net.refractionapi.refraction.helper.runnable.TickableProccesor;
import net.refractionapi.refraction.util.FileUtil;
import net.refractionapi.refraction.util.Pair;

import java.util.HashMap;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public abstract class Tasks<A, T extends Task<A>> {
    private static final HashMap<ResourceLocation, TaskHolder<?, ?>> registered = new HashMap<>();
    private final LevelResource TASK_RESOURCE = FileUtil.createResource("tasks");
    protected final A accessor;
    boolean loaded = false;

    public Tasks(A accessor) {
        this.accessor = accessor;
    }

    public Tasks<A, T> init() {
        new TickableProccesor().process((accessor, bool) -> tickTasks(bool)).start(level());
        return this;
    }

    public void tickTasks(boolean post) {
        if (post) return;
        tasks().forEach((task) -> {
            boolean removed = task.shouldStop() || task.state == Task.State.STOPPED || (task.maxTicks() != -1 && task.tickCount > task.maxTicks());
            if (removed && !task.removed) {
                onStop(task);
                task.onEnd();
                task.state = Task.State.STOPPED;
                task.removed = true; // wait for save before removing from list
                return;
            }
            try {
                task.handleStateTick();
                if (task.state.equals(Task.State.RUNNING)) {
                    task.tick();
                    task.tickCount++;
                }
            } catch (Exception e) {
                task.state = Task.State.STOPPED;
                Refraction.LOGGER.error("Caught exception in ticking Task | stopping", e);
            }
        });
    }

    public void onStop(T task) {
        TagIO io = new TagIO(getDir());
        String id = taskIdString(task);
        CompoundTag loaded = io.load(id);
        loaded.remove(task.uuid.toString());
        io.save(id, loaded);
    }

    public String taskIdString(Task task) {
        return task.id.toString().replace(":", "-");
    }

    public void saveToDisk() {
        if (!loaded) return;
        try {
            TagIO io = new TagIO(getDir());
            tasks().forEach((task) -> {
                String id = taskIdString(task);
                CompoundTag loaded = io.load(id);
                if (!task.state.equals(Task.State.STOPPED)) {
                    CompoundTag serialized = new CompoundTag();
                    task.save(serialized);
                    if (!serialized.contains("uuid")) {
                        Refraction.LOGGER.error("Failed to save task {}, invalid UUID", task.id);
                        return;
                    }
                    loaded.put(task.uuid.toString(), serialized);
                } else {
                    tasks().remove(task);
                    loaded.remove(task.uuid.toString());
                }
                io.save(id, loaded);
            });
        } catch (Exception e) {
            Refraction.LOGGER.error("Error occurred while saving Tasks", e);
        }
    }

    @SuppressWarnings("unchecked")
    public void loadFromDisk(A accessor) {
        try {
            TagIO io = new TagIO(getDir());
            AtomicInteger loaded = new AtomicInteger();
            for (Pair<String, CompoundTag> pair : io.list()) {
                ResourceLocation id = ResourceLocation.parse(pair.first.replace("-", ":").replace(".nbt", ""));
                if (!registered.containsKey(id)) {
                    Refraction.LOGGER.warn("No registered Task found for ID {}", id);
                    continue;
                }
                TaskHolder<?, A> holder = (TaskHolder<?, A>) registered.get(id);
                if (holder == null) {
                    Refraction.LOGGER.warn("Invalid Task holder for {}", id);
                    continue;
                }
                CompoundTag tag = pair.second;
                for (String key : tag.getAllKeys()) {
                    holder.fromNBT(accessor, holder, id, tag.getCompound(key));
                    loaded.getAndIncrement();
                }
            }
            Refraction.LOGGER.info("Loaded {} Tasks", loaded.get());
        } catch (Exception e) {
            Refraction.LOGGER.error("Error occurred while loading Tasks", e);
        }
        loaded = true;
    }

    public abstract List<T> tasks();

    public abstract ServerLevel level();

    public static TaskHolder<?, ?> get(ResourceLocation id) {
        return registered.get(id);
    }

    public static void registerTask(ResourceLocation id, TaskHolder<?, ?> holder) {
        if (registered.containsKey(id)) Refraction.LOGGER.warn("Duplicate Task ID {}", id);
        registered.put(id, holder);
    }

    public void addTask(T task) {
        TaskHolder<?, ?> holder = task.holder;
        if (holder == null) return;
        if (holder.created && holder.allowOnlyOne()) return;
        if (task.accessor != accessor) {
            Refraction.LOGGER.warn(
                    "Task {} accessor doesn't match with current accessor {} != {}",
                    task.id,
                    task.accessor.toString(),
                    accessor.toString()
            );
            return;
        }
        holder.created = true;
        tasks().add(task);
        task.postAdd();
        task.onAdd();
        if (task.tickCount == 0) task.onStart();
    }

    public void removeTaskOfType(Class<Task<?>> taskClass) {
        tasks().removeIf(taskClass::isInstance);
    }

    public String getDir() {
        return "%s/%s".formatted(level().getServer().getWorldPath(TASK_RESOURCE).toFile(), getSubDir());
    }

    public String getSubDir() {
        return level().dimensionTypeRegistration().getRegisteredName().replaceAll("[^a-zA-Z0-9\\.\\-]", "_");
    }
}
