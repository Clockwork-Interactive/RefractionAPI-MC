package net.refractionapi.refraction.helper.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.BoolArgumentType;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.refractionapi.refraction.Refraction;

public class RDebugCommand {

    public RDebugCommand(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("RDebug").requires(context ->
                        context.hasPermission(2)
                ).then(Commands.literal("set")
                        .then(Commands.argument("enabled", BoolArgumentType.bool())
                        .executes(context -> enableDebugger(BoolArgumentType.getBool(context, "enabled"))))
                )
        );
    }

    private int enableDebugger(boolean enabled) {
        Refraction.debugTools = enabled;
        return 1;
    }

}
