package net.refractionapi.refraction.events;

import net.minecraft.world.level.LevelAccessor;

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

}
