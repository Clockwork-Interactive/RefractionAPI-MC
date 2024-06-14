package net.refractionapi.refraction.sound;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.refractionapi.refraction.networking.RefractionMessages;
import net.refractionapi.refraction.networking.S2C.AttachTickableSoundS2CPacket;
import net.refractionapi.refraction.networking.S2C.PlayLocalSoundS2CPacket;
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

    public static void attachSound(Player player, String sound, CompoundTag serialized) {
        if (player instanceof ServerPlayer serverPlayer)
            RefractionMessages.sendToPlayer(new AttachTickableSoundS2CPacket(sound, serialized), serverPlayer);
    }

}
