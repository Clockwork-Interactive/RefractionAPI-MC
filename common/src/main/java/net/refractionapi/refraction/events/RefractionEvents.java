package net.refractionapi.refraction.events;

import net.minecraft.client.gui.LayeredDraw;
import net.minecraft.world.level.LevelAccessor;
import net.refractionapi.refraction.cutscenes.client.CinematicBars;
import net.refractionapi.refraction.platform.RefractionServices;
import net.refractionapi.refraction.quest.client.QuestRenderer;
import org.apache.logging.log4j.util.InternalApi;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Consumer;

public interface RefractionEvents {

    Set<Runnable> serverProcesses = new HashSet<>();
    Set<Consumer<LevelAccessor>> levelProcesses = new HashSet<>();
    Set<Runnable> serverStoppingListeners = new HashSet<>();
    Set<Consumer<LayeredDraw>> registerLayers = new HashSet<>();

    default void addServerProcess(Runnable runnable) {
        serverProcesses.add(runnable);
    }

    default void addServerStoppingListener(Runnable runnable) {
        serverStoppingListeners.add(runnable);
    }

    default void addLevelProcess(Consumer<LevelAccessor> runnable) {
        levelProcesses.add(runnable);
    }

    default void addLayerRegistry(Consumer<LayeredDraw> layer) {
        registerLayers.add(layer);
    }

    @InternalApi
    default void registerLayers(LayeredDraw layeredDraw) {
        registerLayers.forEach((layer) -> layer.accept(layeredDraw));
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

     default void registerOverlays() {
        RefractionServices.EVENTS.addLayerRegistry((layer) -> {
            layer.add(new LayeredDraw().add(CinematicBars::bars), () -> true);
            layer.add(new LayeredDraw().add(QuestRenderer::quest), () -> true);
        });
    }

}
