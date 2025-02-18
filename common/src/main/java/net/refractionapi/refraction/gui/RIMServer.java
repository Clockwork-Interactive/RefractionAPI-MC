package net.refractionapi.refraction.gui;

import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;
import net.refractionapi.refraction.Refraction;
import net.refractionapi.refraction.events.RefractionEvent;
import net.refractionapi.refraction.events.RefractionEventCaller;
import net.refractionapi.refraction.events.RefractionEvents;
import net.refractionapi.refraction.feature.channel.NamedAPI;
import net.refractionapi.refraction.feature.channel.TwoWayChannel;

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

    private RIMServer(MinecraftServer server) {
        this.server = server;
        this.channel = NamedAPI.create(CHANNEL_NAME).configure((channel -> {
            channel.valid((plr, buf, router) -> plr.hasPermissions(2));
            channel.registerListener("command", this::command);
        })).open(server.overworld());
        REGISTER_CHANNEL.invoker().register(this.channel.channel());
    }

    public int command(Player player, FriendlyByteBuf buf) {
        CommandSourceStack stack = new CommandSourceStack(player, player.position(), player.getRotationVector(), (ServerLevel) player.level(), 2, player.getDisplayName().getString(), player.getDisplayName(), this.server, player);
        String command = buf.readUtf();
        this.server.getCommands().performPrefixedCommand(stack, command);
        return 1;
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
