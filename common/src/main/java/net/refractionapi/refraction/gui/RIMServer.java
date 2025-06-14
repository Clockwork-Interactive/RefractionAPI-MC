package net.refractionapi.refraction.gui;

import com.google.common.collect.Iterators;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;
import net.refractionapi.refraction.Refraction;
import net.refractionapi.refraction.config.RServerConfig;
import net.refractionapi.refraction.events.RefractionEvent;
import net.refractionapi.refraction.events.RefractionEventCaller;
import net.refractionapi.refraction.events.RefractionEvents;
import net.refractionapi.refraction.feature.channel.NamedAPI;
import net.refractionapi.refraction.feature.channel.TwoWayChannel;
import net.refractionapi.refraction.gui.cli.CLIComms;

import java.util.Arrays;
import java.util.Optional;

public class RIMServer {
    public static final ResourceLocation CHANNEL_NAME = Refraction.id("rim_server");
    public static final RefractionEvent<RegisterChannels> REGISTER_CHANNEL = new RefractionEventCaller<>(RegisterChannels.class, listeners -> server -> {
        for (RegisterChannels listener : listeners) {
            listener.register(server);
        }
    });
    private static RIMServer instance = null;
    private final NamedAPI channel;
    private final MinecraftServer server;
    long last = 0L;
    long ticks = 0L;

    private RIMServer(MinecraftServer server) {
        this.server = server;
        new CLIComms(server);
        this.channel = NamedAPI.create(CHANNEL_NAME).configure((channel -> {
            channel.valid((plr, buf, router) -> RServerConfig.isPermitted(plr));
            channel.canSendTo(RServerConfig::isPermitted);
            channel.registerListener("auth", this::isValid);
        })).open(server.overworld());
        REGISTER_CHANNEL.invoker().register(this.channel.channel());
        RefractionEvents.SERVER_TICK.register((post) -> {
            if (!post) return;
            this.serverHealth();
        });
    }

    public int isValid(Player player, FriendlyByteBuf buf) {
        this.channel.channel().respond(this.channel.channel().header(), (data) -> {});
        return 1;
    }

    public void serverHealth() {
        long time = System.currentTimeMillis();
        ticks++;
        if (time - last < 1000) return;
        this.channel.channel().send("health", (buf) -> {
            double tps = ticks;
            last = time;
            ticks = 0;
            int players = this.server.getPlayerCount();
            // iterate over all levels, and count the entities --Zeus
            int entities = Arrays.stream(Iterators.toArray(this.server.getAllLevels().iterator(), ServerLevel.class)).mapToInt(level -> Iterators.size(level.getAllEntities().iterator())).sum();
            int chunks = Arrays.stream(Iterators.toArray(this.server.getAllLevels().iterator(), ServerLevel.class)).mapToInt(level -> level.getChunkSource().getLoadedChunksCount()).sum();
            buf.writeDouble(tps);
            buf.writeInt(players);
            buf.writeInt(entities);
            buf.writeInt(chunks);
        });
    }


    public static void init() {
        RefractionEvents.SERVER_STARTING.register(server -> {
            instance = new RIMServer(server);
        });
    }

    public static Optional<RIMServer> get() {
        return Optional.ofNullable(instance);
    }

    @FunctionalInterface
    public interface RegisterChannels {
        void register(TwoWayChannel channel);
    }
}
