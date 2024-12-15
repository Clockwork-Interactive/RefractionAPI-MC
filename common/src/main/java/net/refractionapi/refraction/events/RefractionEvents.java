package net.refractionapi.refraction.events;

import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.client.gui.LayeredDraw;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.LevelAccessor;
import net.refractionapi.refraction.feature.atda.IAtdaProvider;

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
    RefractionEvent<RegisterCommands> REGISTER_COMMANDS = new RefractionEventCaller<>(RegisterCommands.class, listeners -> stack -> {
        for (RegisterCommands listener : listeners) {
            listener.register(stack);
        }
    });
    RefractionEvent<RegisterAtda> REGISTER_ATDA = new RefractionEventCaller<>(RegisterAtda.class, listeners -> provider -> {
        for (RegisterAtda listener : listeners) {
            listener.register(provider);
        }
    });
    RefractionEvent<PlayerClone> PLAYER_CLONE = new RefractionEventCaller<>(PlayerClone.class, listeners -> (current, old) -> {
        for (PlayerClone listener : listeners) {
            listener.clone(current, old);
        }
    });

    default void registerOverlays() {
        // REGISTER_LAYERS.register(layer -> {
        //     layer.add(new LayeredDraw().add(CinematicBars::bars), () -> true);
        //     layer.add(new LayeredDraw().add(QuestRenderer::quest), () -> true);
        // });
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
    interface PlayerJoin {
        void onJoin(ServerPlayer serverPlayer);
    }

    @FunctionalInterface
    interface LoadLevel {
        void onLoad(LevelAccessor accessor);
    }

    @FunctionalInterface
    interface RegisterLayers {
        void register(LayeredDraw layeredDraw);
    }

    @FunctionalInterface
    interface RegisterCommands {
        void register(CommandDispatcher<CommandSourceStack> registrar);
    }

    @FunctionalInterface
    interface RegisterAtda {
        void register(IAtdaProvider provider);
    }

    @FunctionalInterface
    interface PlayerClone {
        void clone(ServerPlayer current, ServerPlayer old);
    }

}
