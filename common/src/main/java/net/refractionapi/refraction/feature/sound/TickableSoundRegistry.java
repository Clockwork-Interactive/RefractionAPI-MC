package net.refractionapi.refraction.feature.sound;

import com.mojang.datafixers.util.Pair;
import jdk.jfr.Experimental;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.sounds.AbstractTickableSoundInstance;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.LivingEntity;
import net.refractionapi.refraction.Refraction;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Experimental
public class TickableSoundRegistry {

    private static final HashMap<String, SoundEntry<?>> SOUNDS = new HashMap<>();
    private static final HashMap<LivingEntity, List<Pair<String, DeserializedSound>>> cachedSounds = new HashMap<>();

    public static <T extends DeserializedSound> void register(String string, Class<T> sound) {
        SOUNDS.put(string, new SoundEntry<>(sound));
    }

    public static void attach(String sound, LivingEntity livingEntity, CompoundTag tag, String cacheName) {
        SoundEntry<?> soundEntry = SOUNDS.get(sound);
        if (soundEntry == null) {
            return;
        }
        DeserializedSound soundInstance;
        try {
            soundInstance = (DeserializedSound) soundEntry.sound.getConstructor(LivingEntity.class, CompoundTag.class).newInstance(livingEntity, tag);
        } catch (Exception e) {
            Refraction.LOGGER.debug("Failed to create sound instance for {}", sound);
            return;
        }
        if (!cacheName.isEmpty()) {
            cachedSounds.computeIfAbsent(soundInstance.entity, (entity) -> new ArrayList<>()).add(new Pair<>(cacheName, soundInstance));
        }
        soundInstance.isPlaying = true;
        Minecraft.getInstance().getSoundManager().play(soundInstance);
    }

    public static void stop(LivingEntity living, String cacheName) {
        if (living == null) return;
        DeserializedSound sound = cachedSounds.get(living).stream().filter(pair -> pair.getFirst().equals(cacheName)).map(Pair::getSecond).findFirst().orElse(null);
        if (sound != null) {
            sound.isPlaying = false;
            cachedSounds.get(living).removeIf(pair -> pair.getFirst().equals(cacheName));
        }
    }


    private record SoundEntry<T extends AbstractTickableSoundInstance>(Class<T> sound) {
    }

}
