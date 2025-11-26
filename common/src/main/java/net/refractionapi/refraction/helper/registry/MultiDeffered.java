package net.refractionapi.refraction.helper.registry;

import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;

import java.util.function.Supplier;

public record MultiDeffered<I extends Item, B extends Block>(Supplier<B> block, Supplier<I> item) {
}
