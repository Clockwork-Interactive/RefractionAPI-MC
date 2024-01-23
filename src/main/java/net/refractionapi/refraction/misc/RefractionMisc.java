package net.refractionapi.refraction.misc;

import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.resources.sounds.EntityBoundSoundInstance;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.refractionapi.refraction.networking.RefractionMessages;
import net.refractionapi.refraction.networking.S2C.EnablePlayerMovementS2CPacket;
import net.refractionapi.refraction.networking.S2C.PlayLocalSoundS2CPacket;
import net.refractionapi.refraction.runnable.RunnableCooldownHandler;

import java.util.List;

public class RefractionMisc {

    public static final RandomSource random = RandomSource.create();

    public static void enableMovement(ServerPlayer player, boolean canMove) {
        RefractionMessages.sendToPlayer(new EnablePlayerMovementS2CPacket(canMove), player);
    }

    public static void enableMovement(ServerPlayer player, int ticks) {
        enableMovement(player, false);
        RunnableCooldownHandler.addDelayedRunnable(() -> enableMovement(player, true), ticks);
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
