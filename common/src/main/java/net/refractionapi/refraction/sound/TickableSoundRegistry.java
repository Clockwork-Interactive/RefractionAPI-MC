package net.refractionapi.refraction.sound;

import jdk.jfr.Experimental;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.sounds.AbstractTickableSoundInstance;
import net.minecraft.nbt.CompoundTag;
import net.refractionapi.refraction.Refraction;

import java.util.HashMap;

@Experimental
public class TickableSoundRegistry {

    private static final HashMap<String, SoundEntry<?>> SOUNDS = new HashMap<>();

    public static <T extends DeserializedSound> void register(String string, Class<T> sound) {
        SOUNDS.put(string, new SoundEntry<>(sound));
    }

    public static void attach(String sound, CompoundTag tag) {
        SoundEntry<?> soundEntry = SOUNDS.get(sound);
        if (soundEntry == null) {
            return;
        }
        DeserializedSound soundInstance;
        try {
            soundInstance = (DeserializedSound) soundEntry.sound.getConstructor(CompoundTag.class).newInstance(tag);
        } catch (Exception e) {
            Refraction.LOGGER.debug("Failed to create sound instance for {}", sound);
            return;
        }
        soundInstance.isPlaying = true;
        Minecraft.getInstance().getSoundManager().play(soundInstance);
    }


    private record SoundEntry<T extends AbstractTickableSoundInstance>(Class<T> sound) {
    }

}
