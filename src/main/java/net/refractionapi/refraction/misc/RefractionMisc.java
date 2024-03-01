package net.refractionapi.refraction.misc;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.RandomSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.refractionapi.refraction.mixininterfaces.ILivingEntity;
import net.refractionapi.refraction.networking.RefractionMessages;
import net.refractionapi.refraction.networking.S2C.EnablePlayerMovementS2CPacket;
import net.refractionapi.refraction.networking.S2C.PlayLocalSoundS2CPacket;
import net.refractionapi.refraction.runnable.RunnableCooldownHandler;

import java.util.List;

public class RefractionMisc {

    public static final RandomSource random = RandomSource.create();

    public static void enableMovement(LivingEntity livingEntity, boolean canMove) {
        if (livingEntity instanceof ServerPlayer serverPlayer) {
            RefractionMessages.sendToPlayer(new EnablePlayerMovementS2CPacket(canMove), serverPlayer);
        } else if (!(livingEntity instanceof Player) && livingEntity instanceof ILivingEntity) {
            ((ILivingEntity) livingEntity).refractionAPI_MC$enableMovement(canMove);
        }
    }

    public static void enableMovement(LivingEntity livingEntity, int ticks) {
        enableMovement(livingEntity, false);
        RunnableCooldownHandler.addDelayedRunnable(() -> enableMovement(livingEntity, true), ticks);
    }

    public static void playLocalSound(Player player, SoundEvent event) {
        if (player instanceof ServerPlayer serverPlayer) {
            RefractionMessages.sendToPlayer(new PlayLocalSoundS2CPacket(event), serverPlayer);
        }
    }

    public static <T> T getRandom(List<T> list) {
        return list.get(random.nextIntBetweenInclusive(0, list.size() - 1));
    }

    public static DamageSource damageSource(ResourceKey<DamageType> damageType, Level level) {
        return new DamageSource(level.registryAccess().registryOrThrow(Registries.DAMAGE_TYPE).getHolderOrThrow(damageType));
    }

}
