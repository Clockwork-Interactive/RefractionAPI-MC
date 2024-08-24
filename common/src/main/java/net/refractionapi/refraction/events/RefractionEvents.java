package net.refractionapi.refraction.events;

import net.minecraft.world.level.LevelAccessor;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Consumer;

public interface RefractionEvents {

    Set<Runnable> serverProcesses = new HashSet<>();
    Set<Consumer<LevelAccessor>> levelProcesses = new HashSet<>();
    Set<Runnable> serverStoppingListeners = new HashSet<>();

    default void addServerProcess(Runnable runnable) {
        serverProcesses.add(runnable);
    }

    default void addServerStoppingListener(Runnable runnable) {
        serverStoppingListeners.add(runnable);
    }

    default void addLevelProcess(Consumer<LevelAccessor> runnable) {
        levelProcesses.add(runnable);
    }

    default void serverTick(boolean post) {
        if (post) return;
        for (Runnable runnable : serverProcesses) {
            runnable.run();
        }
    }

    default void levelTick(LevelAccessor accessor, boolean post) {
        for (Consumer<LevelAccessor> consumer : levelProcesses) {
            consumer.accept(accessor);
        }
    }

    default void serverStopping() {
        for (Runnable runnable : serverStoppingListeners) {
            runnable.run();
        }
    }

}
