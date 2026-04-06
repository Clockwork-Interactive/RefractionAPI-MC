package net.refractionapi.refraction.feature.examples.subdivision;

import net.minecraft.world.level.block.Blocks;
import net.refractionapi.refraction.Refraction;
import net.refractionapi.refraction.feature.subdivision.SubdivisionRegistry;
import net.refractionapi.refraction.feature.subdivision.SubdivisionSet;

public class ExampleSubdivisionRegistry {
    public static final SubdivisionSet EXAMPLE = SubdivisionRegistry.register("example", (set) -> {
        set.maxDepth(256);
        var base = set.createBlockSet("base", (blocks) -> {
            blocks.add(Blocks.BASALT.defaultBlockState(), 1);
        });

        // TODO subsets --Zeus

        set.addOrigin(Refraction.id("test1"), (configurer -> {
            configurer.setRandomBlocks(0, base);
        }));
        set.add(Refraction.id("test2"));
        set.add(Refraction.id("test3"));
        set.add(Refraction.id("test4"));
        set.add(Refraction.id("test5"), (configurer -> {
            configurer.setWeight(2.0F);
        }));
    });

    public static final SubdivisionSet TEST = SubdivisionRegistry.register("test", (set) -> {
        set.maxDepth(64);
        set.addOrigin(Refraction.id("t1"));
        set.add(Refraction.id("t2"));
        set.add(Refraction.id("t3"));
        set.add(Refraction.id("t4"));
        set.add(Refraction.id("t5"), (configurer -> {
            configurer.setBudget(1);
        }));
    });

    public static void init() {

    }
}
