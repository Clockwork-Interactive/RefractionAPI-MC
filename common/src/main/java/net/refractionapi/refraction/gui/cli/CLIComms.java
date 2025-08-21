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
import net.refractionapi.refraction.feature.task.Task;
import net.refractionapi.refraction.feature.task.TaskHolder;
import net.refractionapi.refraction.feature.task.Tasks;

import java.util.List;

public record CLIComms(MinecraftServer server) {
    private static CLIComms instance;
    public static final ResourceLocation ID = Refraction.id("cli_comms");
    public static final ThreadedAPI api = NamedAPI.create(ID)
            .preConfigure((nap) -> RefractionEvents.REGISTER_CLI.invoker().configure(nap))
            .configureServer((c) -> {
                c.valid((plr, buf, id) -> RServerConfig.isPermitted(plr));
                c.registerListener("command", instance()::command);
                c.registerListener("tasks", instance()::tasks);
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

    public int tasks(Player player, FriendlyByteBuf buf) {
        if (!(player.level() instanceof ServerLevel serverLevel)) return 0;
        String mode = buf.readUtf();
        switch (mode) {
            case "run" -> {
                ResourceLocation id = ResourceLocation.parse(buf.readUtf());
                TaskHolder<?> holder = Tasks.get(id);
                if (holder == null) return 0;
                holder.create(serverLevel);
            }
            case "request" -> {
                Tasks tasks = Tasks.get(serverLevel);
                List<Task> taskList = tasks.tasks();
                int size = taskList.size();
                api.channel().respond(api.channel().header(), (send) -> {
                    send.writeInt(size);
                    taskList.forEach((task) -> send.writeUtf("%s - %s [%s/%s]".formatted(
                            task.id(),
                            task.holder().taskDesc,
                            task.ticks(),
                            task.maxTickString()
                    )));
                });
            }
        }
        return 1;
    }

    public static CLIComms instance() {
        return instance;
    }

    public static void init() {

    }
}