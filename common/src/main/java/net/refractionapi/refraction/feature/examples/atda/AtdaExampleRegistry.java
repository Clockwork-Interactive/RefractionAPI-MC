package net.refractionapi.refraction.feature.examples.atda;

import net.minecraft.world.entity.LivingEntity;
import net.refractionapi.refraction.Refraction;
import net.refractionapi.refraction.config.RRuntimeConfig;
import net.refractionapi.refraction.feature.atda.Atda;
import net.refractionapi.refraction.feature.atda.AtdaRegistrar;
import net.refractionapi.refraction.feature.atda.EntityAtda;

public class AtdaExampleRegistry {
    public static EntityAtda<LivingEntity, AtdaExampleData> EXAMPLE = AtdaRegistrar.registerEntity(LivingEntity.class, Refraction.MOD_ID, "example");

    public static void init() {
        if (!RRuntimeConfig.debugTools) return;
        AtdaRegistrar.registerProvider(LivingEntity.class, EXAMPLE, new AtdaExampleProvider());
    }
}
