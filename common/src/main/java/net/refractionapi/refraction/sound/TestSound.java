package net.refractionapi.refraction.sound;

import net.minecraft.client.Minecraft;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;

import java.util.function.Consumer;

public class TestSound extends DeserializedSound {

    private final Entity entity;

    protected TestSound(CompoundTag tag, Consumer<PlayableTickableSound> onTick, Consumer<PlayableTickableSound> onStop) {
        super(tag, SoundEvents.ALLAY_DEATH, SoundSource.PLAYERS, RandomSource.create(), onTick, onStop);
        int entityId = tag.getInt("entityId");
        this.entity = Minecraft.getInstance().level.getEntity(entityId);
    }

    public void tick() {
        this.onTick.accept(this);
        if (this.entity == null || !this.isPlaying) {
            this.stop();
            this.onStop.accept(this);
            return;
        }
        if (this.entity.isAlive()) {
            this.x = (float) this.entity.getX();
            this.y = (float) this.entity.getY();
            this.z = (float) this.entity.getZ();
        }
    }

}
