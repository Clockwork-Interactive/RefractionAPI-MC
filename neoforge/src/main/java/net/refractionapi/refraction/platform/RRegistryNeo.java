package net.refractionapi.refraction.platform;

import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.refractionapi.refraction.Refraction;
import net.refractionapi.refraction.helper.registry.RRegister;
import net.refractionapi.refraction.helper.registry.RRegistry;

import java.util.function.Supplier;

public class RRegistryNeo implements RRegistry {
    public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(Refraction.MOD_ID);
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(Refraction.MOD_ID);

    @Override
    public RRegister<Block> registerBlock(String id, Supplier<Block> block) {
        var reg = new RRegister<>(id, BLOCKS.register(id, block));
        ITEMS.register(id, () -> new BlockItem(reg.get(), new Item.Properties()));
        return reg;
    }

    @Override
    public RRegister<Item> registerItem(String id, Supplier<Item> item) {
        return new RRegister<>(id, ITEMS.register(id, item));
    }
}
