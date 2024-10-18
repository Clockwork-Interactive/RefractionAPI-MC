package net.refractionapi.refraction.feature.sound;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.refractionapi.refraction.networking.RefractionMessages;
import net.refractionapi.refraction.networking.S2C.AttachTickableSoundS2CPacket;
import net.refractionapi.refraction.networking.S2C.PlayLocalSoundS2CPacket;
import net.refractionapi.refraction.networking.S2C.StopTickingSoundS2CPacket;
import net.refractionapi.refraction.networking.S2C.TrackingSoundS2CPacket;

public class SoundUtil {

    public static void playTrackingSound(LivingEntity entity, SoundEvent event, boolean looping, int ticks) {
        RefractionMessages.sendToAllTracking(new TrackingSoundS2CPacket(entity, event, looping, ticks), entity);
    }

        public static void playTrackingSound(LivingEntity entity, SoundEvent event) {
            playTrackingSound(entity, event, false, -1);
        }

        public static void playLocalSound(Player player, SoundEvent event) {
            if (player instanceof ServerPlayer serverPlayer) {
                RefractionMessages.sendToPlayer(new PlayLocalSoundS2CPacket(event), serverPlayer);
            }
        }

    public static void attachSound(LivingEntity living, String sound, CompoundTag serialized, String cacheName) {
        RefractionMessages.sendToAllTracking(new AttachTickableSoundS2CPacket(sound, living, serialized, cacheName), living);
    }
    public static void attachSound(LivingEntity living, String sound, CompoundTag serialized) {
        attachSound(living, sound, serialized, "");
    }
    public static void attachSound(LivingEntity living, String sound) {
        attachSound(living, sound, new CompoundTag());
    }
    public static void stopSound(LivingEntity living, String cacheName) {
        RefractionMessages.sendToAllTracking(new StopTickingSoundS2CPacket(living, cacheName), living);
    }

}
