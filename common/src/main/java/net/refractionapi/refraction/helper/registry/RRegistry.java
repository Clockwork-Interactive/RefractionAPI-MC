package net.refractionapi.refraction.helper.registry;

import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;

import java.util.function.Supplier;

public interface RRegistry {

    RRegister<Block> registerBlock(String id, Supplier<Block> block);

    RRegister<Item> registerItem(String id, Supplier<Item> item);

}
