package net.refractionapi.refraction.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.BoolArgumentType;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.refractionapi.refraction.Refraction;
import net.refractionapi.refraction.config.RRuntimeConfig;
import net.refractionapi.refraction.feature.atda.Atda;
import net.refractionapi.refraction.feature.atda.IAtdaProvider;
import net.refractionapi.refraction.util.TaxMan;

public class RDebugCommand {
    public RDebugCommand(CommandDispatcher<CommandSourceStack> dispatcher) {
        var command = Commands.literal("rdebug");
        command.requires(context -> context.hasPermission(2));
        var setCommand = Commands.literal("set");
        var setArg = Commands.argument("enabled", BoolArgumentType.bool());
        setArg.executes(context -> enableDebugger(context.getSource(), BoolArgumentType.getBool(context, "enabled")));
        var dumpAtda = Commands.literal("dumpAtda");
        var entityArg = Commands.argument("dumpAtda", EntityArgument.entity());
        entityArg.executes(context -> {
            var entity = EntityArgument.getEntity(context, "dumpAtda");
            var providers = Atda.getAllProvidersFor(entity);
            for (var provider : providers) {
                var providerClass = provider.getClass();
                context.getSource().sendSuccess(() -> {
                    var taxMan = TaxMan.literal("%s".formatted(providerClass.getSimpleName())).newLine();
                    var tag = new CompoundTag();
                    provider.serialize(tag);
                    taxMan.append(Component.literal(tag.toString()));
                    return taxMan.component();
                }, false);
            }
            return 1;
        });
        setCommand.then(setArg);
        dumpAtda.then(entityArg);
        command.then(setCommand);
        command.then(dumpAtda);
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
