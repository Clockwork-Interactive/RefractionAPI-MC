package net.refractionapi.refraction.helper.registry;

import net.minecraft.world.level.block.Block;
import net.refractionapi.refraction.platform.RefractionServices;

import java.util.function.Supplier;

public class RBlocks {
    public static RRegister<Block> DOORWAY = register("doorway", () -> new Block(Block.Properties.of().noCollission().noLootTable().noOcclusion()));
    public static RRegister<Block> RANDOMIZER = register("block_randomizer", () -> new Block(Block.Properties.of().noLootTable()));
    public static RRegister<Block> STRUCT = register("struct_randomizer", () -> new Block(Block.Properties.of().noLootTable()));

    public static RRegister<Block> register(String id, Supplier<Block> block) {
        return RefractionServices.REGISTRY.registerBlock(id, block);
    }

    public static void init() {

    }
}
