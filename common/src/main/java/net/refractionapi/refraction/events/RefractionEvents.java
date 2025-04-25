package net.refractionapi.refraction.events;

import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.LevelAccessor;
import net.refractionapi.refraction.feature.atda.IAtdaProvider;
import net.refractionapi.refraction.feature.channel.NamedAPI;
import net.refractionapi.refraction.feature.channel.TwoWayChannel;

public interface RefractionEvents {
    RefractionEvent<LoadLevel> LOAD_LEVEL = new RefractionEventCaller<>(LoadLevel.class, listeners -> world -> {
        for (LoadLevel listener : listeners) {
            listener.onLoad(world);
        }
    });
    RefractionEvent<CommonTick> COMMON_TICK = new RefractionEventCaller<>(CommonTick.class, listeners -> (server, post) -> {
        for (CommonTick listener : listeners) {
            listener.onTick(server, post);
        }
    });
    RefractionEvent<Tick> SERVER_TICK = new RefractionEventCaller<>(Tick.class, listeners -> post -> {
        COMMON_TICK.invoker().onTick(true, post);
        for (Tick listener : listeners) {
            listener.onTick(post);
        }
    });
    RefractionEvent<Tick> CLIENT_TICK = new RefractionEventCaller<>(Tick.class, listeners -> post -> {
        COMMON_TICK.invoker().onTick(false, post);
        for (Tick listener : listeners) {
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
    RefractionEvent<ServerEvent> SERVER_STARTING = new RefractionEventCaller<>(ServerEvent.class, listeners -> server -> {
        for (ServerEvent listener : listeners) {
            listener.onStart(server);
        }
    });
    RefractionEvent<ServerEvent> SERVER_STARTED = new RefractionEventCaller<>(ServerEvent.class, listeners -> server -> {
        for (ServerEvent listener : listeners) {
            listener.onStart(server);
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
    RefractionEvent<CLIServer> REGISTER_CLI = new RefractionEventCaller<>(CLIServer.class, listeners -> (channel) -> {
        for (CLIServer listener : listeners) {
            listener.configure(channel);
        }
    });

    @FunctionalInterface
    interface Tick {
        void onTick(boolean post);
    }

    @FunctionalInterface
    interface CommonTick {
        void onTick(boolean server, boolean post);
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
    interface ServerEvent {
        void onStart(MinecraftServer server);
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

    @FunctionalInterface
    interface CLIServer {
        void configure(NamedAPI channel);
    }
}
