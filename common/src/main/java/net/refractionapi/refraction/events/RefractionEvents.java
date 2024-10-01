package net.refractionapi.refraction.events;

import net.minecraft.server.level.ServerPlayer;
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
    RefractionEvent<PlayerJoin> PLAYER_JOINED = new RefractionEventCaller<>(PlayerJoin.class, listeners -> player -> {
        for (PlayerJoin listener : listeners) {
            listener.onJoin(player);
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
    interface PlayerJoin {
        void onJoin(ServerPlayer serverPlayer);
    }

    @FunctionalInterface
    interface LoadLevel {
        void onLoad(LevelAccessor accessor);
    }

}
