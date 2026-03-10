package net.refractionapi.refraction.feature.examples.atda;

import net.minecraft.world.entity.LivingEntity;
import net.refractionapi.refraction.config.RRuntimeConfig;
import net.refractionapi.refraction.feature.atda.Atda;

public class AtdaExampleRegistry {
    public static Atda<LivingEntity, AtdaExampleData> EXAMPLE = Atda.register(LivingEntity.class, "example");

    public static void init() {
        if (!RRuntimeConfig.debugTools) return;
        Atda.register(LivingEntity.class, EXAMPLE, new AtdaExampleProvider());
    }
}
