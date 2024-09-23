package net.refractionapi.refraction.events;

import net.minecraft.client.gui.LayeredDraw;
import net.minecraft.world.level.LevelAccessor;
import net.refractionapi.refraction.cutscenes.client.CinematicBars;
import net.refractionapi.refraction.quest.client.QuestRenderer;

public interface RefractionEvents {

    RefractionEvent<LoadLevel> LOAD_LEVEL = new RefractionEventCaller<>(LoadLevel.class, listeners -> world -> {
        for (LoadLevel listener : listeners) {
            listener.onLoad(world);
        }
    });
    RefractionEvent<ServerTick> SERVER_TICK = new RefractionEventCaller<>(ServerTick.class, listeners -> post -> {
        for (ServerTick listener : listeners) {
            listener.onTick(post);
        }
    });
    RefractionEvent<LevelTick> LEVEL_TICK = new RefractionEventCaller<>(LevelTick.class, listeners -> (world, post) -> {
        for (LevelTick listener : listeners) {
            listener.onTick(world, post);
        }
    });
    RefractionEvent<ServerStopping> SERVER_STOPPING = new RefractionEventCaller<>(ServerStopping.class, listeners -> () -> {
        for (ServerStopping listener : listeners) {
            listener.onStop();
        }
    });
    RefractionEvent<RegisterLayers> REGISTER_LAYERS = new RefractionEventCaller<>(RegisterLayers.class, listeners -> layeredDraw -> {
        for (RegisterLayers listener : listeners) {
            listener.register(layeredDraw);
        }
    });

    default void registerOverlays() {
        REGISTER_LAYERS.register(layer -> {
            layer.add(new LayeredDraw().add(CinematicBars::bars), () -> true);
            layer.add(new LayeredDraw().add(QuestRenderer::quest), () -> true);
        });
    }

    @FunctionalInterface
    interface ServerTick {
        void onTick(boolean post);
    }

    @FunctionalInterface
    interface LevelTick {
        void onTick(LevelAccessor accessor, boolean post);
    }

    @FunctionalInterface
    interface ServerStopping {
        void onStop();
    }

    @FunctionalInterface
    interface LoadLevel {
        void onLoad(LevelAccessor accessor);
    }

    @FunctionalInterface
    interface RegisterLayers {
        void register(LayeredDraw layeredDraw);
    }

}
