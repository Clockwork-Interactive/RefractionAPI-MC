package net.refractionapi.refraction.feature.task;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.refractionapi.refraction.data.RefractionData;
import net.refractionapi.refraction.events.RefractionEvents;
import net.refractionapi.refraction.helper.runnable.TickableProccesor;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class PlayerTasks extends Tasks<ServerPlayer, PlayerTask> {
    private final List<PlayerTask> tasks = new CopyOnWriteArrayList<>();
    private RefractionEvents.ServerEvent event;

    public PlayerTasks(ServerPlayer player) {
        super(player);
    }

    @Override
    public Tasks<ServerPlayer, PlayerTask> init() {
        event = RefractionEvents.ON_SAVE.register((server) -> this.saveToDisk());
        new TickableProccesor().shouldRun(accessor::isAlive)
                .process((accessor, bool) -> tickTasks(bool))
                .onStop(() -> RefractionEvents.ON_SAVE.unregister(event))
                .start(level());
        return this;
    }

    public static PlayerTasks get(ServerPlayer player) {
        RefractionData data = RefractionData.get(player);
        PlayerTasks previous = data.tasks;
        data.tasks = data.tasks == null ? new PlayerTasks(player) : data.tasks;
        if (previous == null) {
            data.tasks.loadFromDisk(player);
            data.tasks.init();
        }
        return data.tasks;
    }

    public static List<PlayerTask> getPlayerTasks(ServerPlayer player) {
        return get(player).tasks();
    }

    @SuppressWarnings("unchecked")
    public static <T extends PlayerTask> List<T> getPlayerTasks(ServerPlayer player, TaskHolder<?, ?> holder) {
        return (List<T>) getPlayerTasks(player).stream().filter((task) -> task.holder() == holder).toList();
    }

    public static boolean hasTask(ServerPlayer serverPlayer, TaskHolder<?, ?> holder) {
        return !getPlayerTasks(serverPlayer, holder).isEmpty();
    }

    @Override
    public List<PlayerTask> tasks() {
        return tasks;
    }

    @Override
    public ServerLevel level() {
        return accessor.serverLevel();
    }

    @Override
    public String getSubDir() {
        return accessor.getUUID().toString();
    }
}
