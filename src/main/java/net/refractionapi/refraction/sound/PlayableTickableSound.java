package net.refractionapi.refraction.sound;

import net.minecraft.client.resources.sounds.AbstractTickableSoundInstance;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;

import java.util.function.Consumer;

public abstract class PlayableTickableSound extends AbstractTickableSoundInstance {

    public boolean isPlaying = false;

    protected PlayableTickableSound(SoundEvent p_235076_, SoundSource soundSource, RandomSource randomSource) {
        super(p_235076_, soundSource, randomSource);
    }

}
