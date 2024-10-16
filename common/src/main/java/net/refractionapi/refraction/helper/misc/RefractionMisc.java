package net.refractionapi.refraction.helper.misc;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.RandomSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.refractionapi.refraction.events.event.RefractionCommonData;
import net.refractionapi.refraction.mixininterfaces.ILivingEntity;
import net.refractionapi.refraction.networking.RefractionMessages;
import net.refractionapi.refraction.networking.S2C.EnablePlayerMovementS2CPacket;
import net.refractionapi.refraction.helper.runnable.RunnableCooldownHandler;
import oshi.util.tuples.Pair;

import java.util.List;

public class RefractionMisc {

    public static final RandomSource random = RandomSource.create();

    public static void enableMovement(LivingEntity livingEntity, boolean canMove, boolean teleport) {

        RefractionCommonData.frozenEntities.compute(livingEntity, (k, v) -> {
            if (!canMove) {
                livingEntity.hurtMarked = true;
                livingEntity.teleportTo(livingEntity.position().x, livingEntity.position().y, livingEntity.position().z);
            }
            return canMove ? null : new Pair<>(livingEntity.position(), teleport);
        });

        if (livingEntity instanceof ServerPlayer serverPlayer) {
            RefractionMessages.sendToPlayer(new EnablePlayerMovementS2CPacket(canMove), serverPlayer);
        } else if (!(livingEntity instanceof Player) && livingEntity instanceof ILivingEntity) {
            ((ILivingEntity) livingEntity).refractionAPI_MC$enableMovement(canMove);
        }
    }

    public static void enableMovement(LivingEntity livingEntity, boolean canMove) {
        enableMovement(livingEntity, canMove, false);
    }


    public static void enableMovement(LivingEntity livingEntity, int ticks, boolean teleport) {
        enableMovement(livingEntity, false, teleport);
        RunnableCooldownHandler.addDelayedRunnable(() -> enableMovement(livingEntity, true, teleport), ticks);
    }

    public static void enableMovement(LivingEntity livingEntity, int ticks) {
        enableMovement(livingEntity, ticks, true);
    }

    public static boolean isLocked(LivingEntity livingEntity) {
        return RefractionCommonData.frozenEntities.containsKey(livingEntity);
    }

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
