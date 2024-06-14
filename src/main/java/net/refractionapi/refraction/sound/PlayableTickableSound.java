package net.refractionapi.refraction.sound;

import net.minecraft.client.resources.sounds.AbstractTickableSoundInstance;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;

import java.util.function.Consumer;

public abstract class PlayableTickableSound extends AbstractTickableSoundInstance {

    public boolean isPlaying = false;
    protected final Consumer<PlayableTickableSound> onTick;
    protected final Consumer<PlayableTickableSound> onStop;

    protected PlayableTickableSound(SoundEvent p_235076_, SoundSource p_235077_, RandomSource p_235078_, Consumer<PlayableTickableSound> onTick, Consumer<PlayableTickableSound> onStop) {
        super(p_235076_, p_235077_, p_235078_);
        this.onTick = onTick;
        this.onStop = onStop;
    }

    @Override
    public void tick() {
        this.onTick.accept(this);
    }

}
