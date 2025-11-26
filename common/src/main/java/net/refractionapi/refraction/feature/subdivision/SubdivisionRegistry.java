package net.refractionapi.refraction.feature.subdivision;

import net.minecraft.resources.ResourceLocation;
import net.refractionapi.refraction.helper.clazz.RModRegistrar;

import java.util.HashMap;
import java.util.function.Consumer;

public class SubdivisionRegistry {
    private static final HashMap<ResourceLocation, SubdivisionSet> registries = new HashMap<>();

    public static SubdivisionSet register(ResourceLocation id, Consumer<SubdivisionSet> consumer) {
        var set = new SubdivisionSet();
        consumer.accept(set);
        registries.put(id, set);
        return set;
    }

    public static SubdivisionSet register(String id, Consumer<SubdivisionSet> consumer) {
        return register(RModRegistrar.id(id, 2), consumer);
    }

    public static SubdivisionSet get(ResourceLocation id) {
        return registries.get(id);
    }
}
