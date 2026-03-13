package net.refractionapi.refraction.commands;

import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.refractionapi.refraction.feature.reconfig.ReConfigurer;
import net.refractionapi.refraction.util.Side;

public class RReConfigCommand {
    public RReConfigCommand(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("reconfig").requires(context ->
                        context.hasPermission(2)
                ).then(Commands.literal("reload-all")
                        .executes(context -> reloadAllConfigs(context.getSource()))
                )
        );
    }

    private int reloadAllConfigs(CommandSourceStack stack) {
        for (Side side : Side.values())
            ReConfigurer.reload(side, (id, builder) ->
                    stack.sendSuccess(() -> Component.literal("Reloaded %s %s".formatted(id, side)), true)
            );
        return 1;
    }
}
