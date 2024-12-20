package net.refractionapi.refraction.helper.registry.item.items;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.context.UseOnContext;
import net.refractionapi.refraction.Refraction;
import net.refractionapi.refraction.feature.examples.atda.AtdaExampleRegistry;

public class RandomItem extends Item {

    public RandomItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        Player player = context.getPlayer();
        if (player.level().isClientSide)
            AtdaExampleRegistry.EXAMPLE.get(player).ifPresent(data -> {
                data.exampleData++;
                Refraction.LOGGER.info("Example client data: {}", data.exampleData);
            });
        if (!(player instanceof ServerPlayer serverPlayer)) return super.useOn(context);
        AtdaExampleRegistry.EXAMPLE.get(serverPlayer).ifPresent(data -> {
            data.exampleData++;
            Refraction.LOGGER.info("Example server data: {}", data.exampleData);
            data.sync(player);
        });
        return super.useOn(context);
    }

}
