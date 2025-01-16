package net.refractionapi.refraction.helper.misc;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.RandomSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;

import java.util.List;

public class RefractionMisc {
    public static final RandomSource random = RandomSource.create();

    public static <T> T getRandom(List<T> list) {
        return list.get(random.nextIntBetweenInclusive(0, list.size() - 1));
    }

    public static DamageSource damageSource(ResourceKey<DamageType> damageType, Level level) {
        return new DamageSource(level.registryAccess().registryOrThrow(Registries.DAMAGE_TYPE).getHolderOrThrow(damageType));
    }

    public static DamageSource damageSource(ResourceKey<DamageType> damageType, LivingEntity livingEntity) {
        return new DamageSource(livingEntity.level().registryAccess().registryOrThrow(Registries.DAMAGE_TYPE).getHolderOrThrow(damageType), livingEntity);
    }
}
