package net.refractionapi.refraction.sound;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.LivingEntity;

import java.util.function.Consumer;

public abstract class DeserializedSound extends PlayableTickableSound {

    public final LivingEntity entity;

    protected DeserializedSound(LivingEntity living, CompoundTag tag, SoundEvent soundEvent, SoundSource soundSource, RandomSource randomSource) {
        super(soundEvent, soundSource, randomSource);
        this.entity = living;
    }

}
