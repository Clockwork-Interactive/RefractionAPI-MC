package net.refractionapi.refraction.feature.examples.subdivision;

import net.minecraft.world.level.block.Blocks;
import net.refractionapi.refraction.Refraction;
import net.refractionapi.refraction.feature.subdivision.SubdivisionRegistry;
import net.refractionapi.refraction.feature.subdivision.SubdivisionSet;

public class ExampleSubdivisionRegistry {
    public static final SubdivisionSet EXAMPLE = SubdivisionRegistry.register("example", (set) -> {
        var base = set.createBlockSet("base", (blocks) -> {
            blocks.add(Blocks.BASALT.defaultBlockState(), 1);
        });

        // TODO subsets --Zeus

        set.addOrigin(Refraction.id("test1"), (configurer -> {
            configurer.setRandomBlocks(0, base);
        }));
        set.add(Refraction.id("test2"), (configurer -> {
        }));
        set.add(Refraction.id("test3"), (configurer -> {
        }));
        set.add(Refraction.id("test4"), (configurer -> {
        }));
        set.add(Refraction.id("test5"), (configurer -> {
        }));
    });

    public static void init() {

    }
}
