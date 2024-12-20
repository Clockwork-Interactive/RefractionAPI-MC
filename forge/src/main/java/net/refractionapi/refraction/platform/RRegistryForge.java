package net.refractionapi.refraction.platform;

import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.refractionapi.refraction.Refraction;
import net.refractionapi.refraction.helper.registry.RRegister;
import net.refractionapi.refraction.helper.registry.RRegistry;

import java.util.function.Supplier;

public class RRegistryForge implements RRegistry {

    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, Refraction.MOD_ID);
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, Refraction.MOD_ID);

    @Override
    public RRegister<Block> registerBlock(String id, Supplier<Block> block) {
        return new RRegister<>(id, BLOCKS.register(id, block));
    }

    @Override
    public RRegister<Item> registerItem(String id, Supplier<Item> item) {
        return new RRegister<>(id, ITEMS.register(id, item));
    }

}
