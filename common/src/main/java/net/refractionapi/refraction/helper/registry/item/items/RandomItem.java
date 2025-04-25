package net.refractionapi.refraction.helper.registry.item.items;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.refractionapi.refraction.feature.examples.screen.ExampleScreenRegistry;

public class RandomItem extends Item {
    public RandomItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level pLevel, Player pPlayer, InteractionHand pUsedHand) {
        if (!pLevel.isClientSide) {
            ExampleScreenRegistry.EXAMPLE_SCHEME.open(pPlayer,"test");
        }
        return super.use(pLevel, pPlayer, pUsedHand);
    }
}