package net.refractionapi.refraction.sound;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;

import java.util.function.Consumer;

public class DeserializedSound extends PlayableTickableSound {

    protected DeserializedSound(CompoundTag tag, SoundEvent p_235076_, SoundSource p_235077_, RandomSource p_235078_, Consumer<PlayableTickableSound> onTick, Consumer<PlayableTickableSound> onStop) {
        super(p_235076_, p_235077_, p_235078_, onTick, onStop);
    }

}
