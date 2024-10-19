package net.refractionapi.refraction.platform;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.refractionapi.refraction.Refraction;
import net.refractionapi.refraction.helper.registry.RRegister;
import net.refractionapi.refraction.helper.registry.RRegistry;

import java.util.function.Supplier;

public class RRegistryFabric implements RRegistry {

    @Override
    public RRegister<Block> registerBlock(String id, Supplier<Block> block) {
        Block blockInstance = Registry.register(BuiltInRegistries.BLOCK, Refraction.id(id), block.get());
        RRegister<Block> register = new RRegister<>(id, () -> blockInstance);
        return register;
    }

    @Override
    public RRegister<Item> registerItem(String id, Supplier<Item> item) {
        Item itemInstance = Registry.register(BuiltInRegistries.ITEM, Refraction.id(id), item.get());
        RRegister<Item> register = new RRegister<>(id, () -> itemInstance);
        return register;
    }

}
