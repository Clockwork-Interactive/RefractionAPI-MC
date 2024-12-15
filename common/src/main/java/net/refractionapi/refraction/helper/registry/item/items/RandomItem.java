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
        if (!(player instanceof ServerPlayer serverPlayer)) return super.useOn(context);
        AtdaExampleRegistry.EXAMPLE.get(serverPlayer).ifPresent(data -> {
            Refraction.LOGGER.info("Example data: {}", data.exampleData);
            data.exampleData++;
            Refraction.LOGGER.info("Example data: {}", data.exampleData);
        });
        return super.useOn(context);
    }

}
