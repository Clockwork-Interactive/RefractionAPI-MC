package net.refractionapi.refraction.feature.sound;

import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.LivingEntity;

public class TrackingSound extends PlayableTickableSound {

    private final LivingEntity entity;
    private final int loopingTicks;
    private int ticks;

    public TrackingSound(SoundEvent soundEvent, LivingEntity livingEntity, boolean looping, int loopingTicks) {
        super(soundEvent, SoundSource.MASTER, livingEntity.getRandom());
        this.entity = livingEntity;
        this.looping = looping;
        this.loopingTicks = loopingTicks;
        this.volume = 1.0F;
        this.pitch = 1.0F;
    }

    @Override
    public void tick() {
        this.ticks++;
        if (this.loopingTicks != -1 && this.ticks >= this.loopingTicks || !this.isPlaying) {
            this.stop();
            return;
        }
        if (this.entity != null && this.entity.isAlive()) {
            this.x = (float) this.entity.getX();
            this.y = (float) this.entity.getY();
            this.z = (float) this.entity.getZ();
        }
    }

}
