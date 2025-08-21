package net.refractionapi.refraction.feature.task;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.storage.LevelResource;
import net.refractionapi.refraction.Refraction;
import net.refractionapi.refraction.helper.misc.TagIO;
import net.refractionapi.refraction.helper.runnable.TickableProccesor;
import net.refractionapi.refraction.mixininterfaces.ILevel;
import net.refractionapi.refraction.util.FileUtil;
import net.refractionapi.refraction.util.Pair;

import java.util.HashMap;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Minimalist task persistent handling
 */
public class Tasks {
    private static final HashMap<ResourceLocation, TaskHolder<?>> registered = new HashMap<>();
    private final List<Task> tasks = new CopyOnWriteArrayList<>();
    private final LevelResource TASK_RESOURCE = FileUtil.createResource("tasks");
    protected final ServerLevel level;

    public Tasks(ServerLevel level) {
        this.level = level;
        new TickableProccesor().process((accessor, bool) -> tickTasks(bool)).start(level);
    }

    public void tickTasks(boolean post) {
        if (post) return;
        tasks.removeIf((task) -> {
            boolean removed = task.maxTicks() != -1 && task.tickCount > task.maxTicks();
            if (removed) task.onEnd();
            return removed;
        });
        tasks.forEach((task) -> {
            task.handleStateTick();
            if (task.state.equals(Task.State.RUNNING)) {
                task.tick();
                task.tickCount++;
            }
        });
    }

    public void saveToDisk() {
        try {
            TagIO io = new TagIO(getDir());
            tasks.forEach((task) -> {
                String id = task.id.toString().replace(":", "-");
                CompoundTag loaded = io.load(id);
                CompoundTag serialized = new CompoundTag();
                task.serialize(serialized);
                if (!serialized.contains("uuid")) {
                    Refraction.LOGGER.error("Failed to save task {}, invalid UUID", task.id);
                    return;
                }
                loaded.put(task.uuid.toString(), serialized);
                io.save(id, loaded);
            });
        } catch (Exception e) {
            Refraction.LOGGER.error("Error occurred while saving Tasks", e);
        }
    }

    public void loadFromDisk() {
        try {
            TagIO io = new TagIO(getDir());
            AtomicInteger loaded = new AtomicInteger();
            for (Pair<String, CompoundTag> pair : io.list()) {
                ResourceLocation id = ResourceLocation.parse(pair.first.replace("-", ":").replace(".nbt", ""));
                if (!registered.containsKey(id)) {
                    Refraction.LOGGER.warn("No registered Task found for ID {}", id);
                    continue;
                }
                TaskHolder<?> holder = registered.get(id);
                if (holder == null) {
                    Refraction.LOGGER.warn("Invalid Task holder for {}", id);
                    continue;
                }
                CompoundTag tag = pair.second;
                for (String key : tag.getAllKeys()) {
                    holder.fromNBT(level, holder, id, tag.getCompound(key));
                    loaded.getAndIncrement();
                }
            }
            Refraction.LOGGER.info("Loaded {} Tasks", loaded.get());
        } catch (Exception e) {
            Refraction.LOGGER.error("Error occurred while loading Tasks", e);
        }
    }

    public String getDir() {
        return "%s/%s".formatted(level.getServer().getWorldPath(TASK_RESOURCE), level.dimensionTypeRegistration().getRegisteredName().replaceAll("[^a-zA-Z0-9\\.\\-]", "_"));
    }

    public List<Task> tasks() {
        return tasks;
    }

    public static TaskHolder<?> get(ResourceLocation id) {
        return registered.get(id);
    }

    public static void registerTask(ResourceLocation id, TaskHolder<?> holder) {
        if (registered.containsKey(id)) Refraction.LOGGER.warn("Duplicate Task ID {}", id);
        registered.put(id, holder);
    }

    public static Tasks get(ServerLevel level) {
        return level instanceof ILevel iLevel ? iLevel.tasks() : null;
    }

    public void addTask(Task task) {
        TaskHolder<?> holder = task.holder;
        if (holder == null) return;
        if (holder.created && holder.allowOnlyOne()) return;
        if (task.level != level) {
            Refraction.LOGGER.warn(
                    "Task {} level doesn't match with current level {} != {}",
                    task.id,
                    task.level.dimensionTypeRegistration().getRegisteredName(),
                    level.dimensionTypeRegistration().getRegisteredName()
            );
            return;
        }
        holder.created = true;
        tasks.add(task);
        task.onAdd();
        if (task.tickCount == 0) task.onStart();
    }
}
