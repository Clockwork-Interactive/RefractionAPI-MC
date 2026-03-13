package net.refractionapi.refraction.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.commands.arguments.ResourceLocationArgument;
import net.minecraft.commands.arguments.coordinates.BlockPosArgument;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.refractionapi.refraction.feature.subdivision.Subdivision;
import net.refractionapi.refraction.feature.subdivision.SubdivisionPiece;
import net.refractionapi.refraction.feature.subdivision.SubdivisionRegistry;
import net.refractionapi.refraction.feature.subdivision.SubdivisionSet;

public class SubdivisionCommand {
    public SubdivisionCommand(CommandDispatcher<CommandSourceStack> dispatcher) {
        var subdivision = Commands.literal("subdivision");
        subdivision.requires(ctx -> ctx.hasPermission(2));

        var set = Commands.literal("set");
        SuggestionProvider<CommandSourceStack> setSuggest = (ctx, builder) ->
                SharedSuggestionProvider.suggest(SubdivisionRegistry.getIDStrings().stream(), builder);
        set.then(Commands.argument("registry", ResourceLocationArgument.id()).suggests(setSuggest)
                .then(Commands.argument("pos", BlockPosArgument.blockPos())
                        .executes(context ->
                                set(context.getSource(),
                                        ResourceLocationArgument.getId(context, "registry"),
                                        BlockPosArgument.getBlockPos(context, "pos")
                                ))));
        subdivision.then(set);

        var piece = Commands.literal("piece");
        SuggestionProvider<CommandSourceStack> pieceSuggest = (ctx, builder) ->
                SharedSuggestionProvider.suggest(Subdivision.getInstance().getStructIDStrings().stream(), builder);
        piece.then(Commands.argument("registry", ResourceLocationArgument.id()).suggests(pieceSuggest)
                .then(Commands.argument("rotation", IntegerArgumentType.integer())
                        .then(Commands.argument("pos", BlockPosArgument.blockPos())
                        .executes(context ->
                                piece(context.getSource(),
                                        ResourceLocationArgument.getId(context, "registry"),
                                        IntegerArgumentType.getInteger(context, "rotation"),
                                        BlockPosArgument.getBlockPos(context, "pos")
                                )))));
        subdivision.then(piece);

        dispatcher.register(subdivision);
    }

    public int set(CommandSourceStack stack, ResourceLocation registry, BlockPos pos) {
        ServerLevel level = stack.getLevel();
        SubdivisionSet set = SubdivisionRegistry.get(registry);
        if (set == null) {
            stack.sendFailure(Component.literal("Subdivision registry %s does not exist".formatted(registry)));
            return 0;
        }
        Subdivision.generate(set, level, pos);
        return 1;
    }

    public int piece(CommandSourceStack stack, ResourceLocation id, int rotation, BlockPos pos) {
        ServerLevel level = stack.getLevel();
        Subdivision.StructCache piece = Subdivision.getInstance().getPiece(id);
        if (piece == null) {
            stack.sendFailure(Component.literal("Subdivision registry %s does not exist".formatted(id)));
            return 0;
        }
        Subdivision.placePiece(new SubdivisionPiece.Configurer(id), level, pos, rotation);
        return 1;
    }
}
