package net.refractionapi.refraction.helper.registry.item.items;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.refractionapi.refraction.feature.examples.screen.ExampleScreenRegistry;

public class ScreenTestItem extends Item {
    public ScreenTestItem(Properties $$0) {
        super($$0);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (level.isClientSide) {
            ExampleScreenRegistry.EXAMPLE_SCREEN.setScreen("fortnite");
            return InteractionResultHolder.fail(stack);
        }
        if (!player.isCrouching())
            ExampleScreenRegistry.EXAMPLE_SCREEN.setScreen(player, "fortnite");
        return super.use(level, player, hand);
    }
}
