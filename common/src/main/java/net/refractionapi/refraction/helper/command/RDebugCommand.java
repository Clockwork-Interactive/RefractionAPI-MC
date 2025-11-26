package net.refractionapi.refraction.helper.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.BoolArgumentType;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.refractionapi.refraction.Refraction;
import net.refractionapi.refraction.config.RRuntimeConfig;

public class RDebugCommand {
    public RDebugCommand(CommandDispatcher<CommandSourceStack> dispatcher) {
        var command = Commands.literal("rdebug");
        command.requires(context -> context.hasPermission(2));
        var setCommand = Commands.literal("set");
        var setArg = Commands.argument("enabled", BoolArgumentType.bool());
        setArg.executes(context -> enableDebugger(context.getSource(), BoolArgumentType.getBool(context, "enabled")));
        setCommand.then(setArg);
        command.then(setCommand);
        dispatcher.register(command);
    }

    private int enableDebugger(CommandSourceStack stack, boolean enabled) {
        RRuntimeConfig.debugTools = enabled;
        Refraction.syncConfig.syncAll(stack.getLevel());
        stack.sendSuccess(() -> Component.literal("RDebug: " + enabled), true);
        Refraction.LOGGER.info("Debug got {} by {}", enabled ? "enabled" : "disabled", stack.getDisplayName());
        return 1;
    }
}
