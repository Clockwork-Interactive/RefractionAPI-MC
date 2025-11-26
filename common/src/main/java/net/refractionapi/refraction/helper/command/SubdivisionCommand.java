package net.refractionapi.refraction.helper.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.ResourceLocationArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.refractionapi.refraction.feature.subdivision.Subdivision;
import net.refractionapi.refraction.feature.subdivision.SubdivisionPiece;
import net.refractionapi.refraction.feature.subdivision.SubdivisionRegistry;
import net.refractionapi.refraction.feature.subdivision.SubdivisionSet;

public class SubdivisionCommand {
    public SubdivisionCommand(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("subdivision").requires(context ->
                        context.hasPermission(2)
                ).then(Commands.literal("set")
                        .then(Commands.argument("registry", StringArgumentType.greedyString())
                                .executes(context -> set(context.getSource(), StringArgumentType.getString(context, "registry"))))
                ).then(Commands.literal("piece")
                        .then(Commands.argument("registry", ResourceLocationArgument.id())
                                .then(Commands.argument("rotation", IntegerArgumentType.integer())
                                        .executes(context -> piece(context.getSource(), ResourceLocationArgument.getId(context, "registry"), IntegerArgumentType.getInteger(context, "rotation")))))
                )
        );
    }

    public int set(CommandSourceStack stack, String registry) {
        ServerLevel level = stack.getLevel();
        SubdivisionSet set = SubdivisionRegistry.get(ResourceLocation.parse(registry));
        if (set == null) {
            stack.sendFailure(Component.literal("Subdivision registry %s does not exist".formatted(registry)));
            return 0;
        }
        Subdivision.generate(set, level, stack.getPlayer().blockPosition());
        return 1;
    }

    public int piece(CommandSourceStack stack, ResourceLocation id, int rotation) {
        ServerLevel level = stack.getLevel();
        Subdivision.StructCache piece = Subdivision.getInstance().get(id);
        if (piece == null) {
            stack.sendFailure(Component.literal("Subdivision registry %s does not exist".formatted(id)));
            return 0;
        }
        Subdivision.placePiece(new SubdivisionPiece.Configurer(id), level, stack.getPlayer().blockPosition(), rotation);
        return 1;
    }
}
