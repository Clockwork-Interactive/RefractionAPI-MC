package net.refractionapi.refraction.helper.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.BoolArgumentType;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.refractionapi.refraction.Refraction;
import net.refractionapi.refraction.config.RRuntimeConfig;
import net.refractionapi.refraction.feature.reconfig.ReConfigurer;

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
        for (ReConfigurer.Side side : ReConfigurer.Side.values()) ReConfigurer.reload(side);
        return 1;
    }
}
