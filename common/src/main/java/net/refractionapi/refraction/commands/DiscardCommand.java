package net.refractionapi.refraction.commands;

import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;

import java.util.Collection;

public class DiscardCommand {
    public DiscardCommand(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("discard").requires(context ->
                        context.hasPermission(2)
                ).then(Commands.argument("entity", EntityArgument.entities())
                        .executes(context -> discard(context.getSource(), EntityArgument.getEntities(context, "entity"))))
        );
    }

    public int discard(CommandSourceStack s, Collection<? extends Entity> entities) {
        s.sendSuccess(() -> Component.literal("Discarded %s entities!".formatted(entities.size())), true);
        entities.forEach(Entity::discard);
        return 1;
    }
}
