package net.refractionapi.refraction.helper.registry.item.items;

import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.context.UseOnContext;
import net.refractionapi.refraction.Refraction;
import net.refractionapi.refraction.feature.examples.reconfig.ReConfigExample;

public class RandomItem extends Item {
    public RandomItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        Player player = context.getPlayer();
        if (player.level().isClientSide) return InteractionResult.FAIL;
        Refraction.LOGGER.info(String.valueOf(ReConfigExample.EXAMPLE.asMap()));
        return super.useOn(context);
    }
}
