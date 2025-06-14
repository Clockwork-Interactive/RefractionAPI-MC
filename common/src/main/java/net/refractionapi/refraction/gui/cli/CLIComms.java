package net.refractionapi.refraction.gui.cli;

import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;
import net.refractionapi.refraction.Refraction;
import net.refractionapi.refraction.config.RServerConfig;
import net.refractionapi.refraction.events.RefractionEvents;
import net.refractionapi.refraction.feature.channel.NamedAPI;
import net.refractionapi.refraction.feature.channel.ThreadedAPI;

public record CLIComms(MinecraftServer server) {
    private static CLIComms instance;
    public static final ResourceLocation ID = Refraction.id("cli_comms");
    public static final ThreadedAPI api = NamedAPI.create(ID)
            .preConfigure((nap) -> RefractionEvents.REGISTER_CLI.invoker().configure(nap))
            .configureServer((c) -> {
                c.valid((plr, buf, id) -> RServerConfig.isPermitted(plr));
                c.registerListener("command", instance()::command);
            })
            .initCommon();

    public CLIComms {
        instance = this;
    }

    public int command(Player player, FriendlyByteBuf buf) {
        CommandSourceStack stack = new CommandSourceStack(player, player.position(), player.getRotationVector(), (ServerLevel) player.level(), 2, player.getDisplayName().getString(), player.getDisplayName(), this.server, player);
        String command = buf.readUtf();
        server.getCommands().performPrefixedCommand(stack, command);
        return 1;
    }

    public static CLIComms instance() {
        return instance;
    }

    public static void init() {

    }
}