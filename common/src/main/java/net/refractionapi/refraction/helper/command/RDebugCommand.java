package net.refractionapi.refraction.helper.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.BoolArgumentType;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.refractionapi.refraction.Refraction;
import net.refractionapi.refraction.config.RRuntimeConfig;

public class RDebugCommand {
    public RDebugCommand(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("RDebug").requires(context ->
                        context.hasPermission(2)
                ).then(Commands.literal("set")
                        .then(Commands.argument("enabled", BoolArgumentType.bool())
                                .executes(context -> enableDebugger(context.getSource(), BoolArgumentType.getBool(context, "enabled"))))
                )
        );
    }

    private int enableDebugger(CommandSourceStack stack, boolean enabled) {
        RRuntimeConfig.debugTools = enabled;
        Refraction.syncConfig.syncAll(stack.getLevel());
        Refraction.LOGGER.info("Debug got {} by {}", enabled ? "enabled" : "disabled", stack.getDisplayName());
        return 1;
    }
}
