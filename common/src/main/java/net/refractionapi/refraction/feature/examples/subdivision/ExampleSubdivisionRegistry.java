package net.refractionapi.refraction.feature.examples.subdivision;

import net.minecraft.world.level.block.Blocks;
import net.refractionapi.refraction.Refraction;
import net.refractionapi.refraction.feature.subdivision.SubdivisionRegistry;
import net.refractionapi.refraction.feature.subdivision.SubdivisionSet;

public class ExampleSubdivisionRegistry {
    public static final SubdivisionSet EXAMPLE = SubdivisionRegistry.register("example", (set) -> {
        set.setOrigin(Refraction.id("test1"), (configurer -> {
            configurer.setRandomBlocks(0, (random) -> {
                random.add(Blocks.BASALT.defaultBlockState(), 1);
            });
        }));
        set.add(Refraction.id("test2"), (configurer -> {
            configurer.setRandomBlocks(0, (random) -> {
                random.add(Blocks.BASALT.defaultBlockState(), 1);
            });
        }));
        set.add(Refraction.id("test3"), (configurer -> {
            configurer.setRandomBlocks(0, (random) -> {
                random.add(Blocks.BASALT.defaultBlockState(), 1);
            });
        }));
        set.add(Refraction.id("test4"), (configurer -> {
            configurer.setRandomBlocks(0, (random) -> {
                random.add(Blocks.BASALT.defaultBlockState(), 1);
            });
        }));
    });

    public static void init() {

    }
}
