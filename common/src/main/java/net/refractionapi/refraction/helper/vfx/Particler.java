package net.refractionapi.refraction.helper.vfx;

import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.ParticleEngine;
import net.minecraft.world.phys.Vec3;
import net.refractionapi.refraction.helper.registry.RRegister;
import net.refractionapi.refraction.helper.runnable.Runnabler;

import java.util.function.BiConsumer;

public class Particler extends VFXWrapper<Particler> {
    private final RRegister<ParticlerType> particleRegister;
    private BiConsumer<Runnabler, ParticlerParticle> onTick = (runnabler, particle) -> {
    };
    private Vec3 spawn = Vec3.ZERO;
    private ParticlerParticle particlerParticle;
    private int lifetime = 0;
    private float gravity = 0.0F;

    public Particler(VFXer vfXer, RRegister<ParticlerType> particleRegister) {
        super(vfXer);
        this.particleRegister = particleRegister;
    }

    public Particler onTick(BiConsumer<Runnabler, ParticlerParticle> onTick) {
        this.onTick = onTick;
        return this;
    }

    public Particler setSpawn(Vec3 spawn) {
        this.spawn = spawn;
        return this;
    }

    public Particler setLifetime(int lifetime) {
        this.lifetime = lifetime;
        return this;
    }

    public Particler setGravity(float gravity) {
        this.gravity = gravity;
        return this;
    }

    public Particler spawn(Vec3 spawn) {
        this.setSpawn(spawn);
        return this.spawn();
    }

    public ParticlerParticle particle() {
        return this.particlerParticle;
    }

    @Override
    public Particler spawn() {
        ParticleEngine engine = Minecraft.getInstance().particleEngine;
        this.particlerParticle = (ParticlerParticle) engine.createParticle(this.particleRegister.get(), this.spawn.x, this.spawn.y, this.spawn.z, 0, 0, 0);
        if (this.particlerParticle == null) throw new NullPointerException("Failed to create particle %s".formatted(this.particleRegister.getId()));
        this.particlerParticle.onTick(this.onTick);
        this.particlerParticle.setLifetime(this.lifetime);
        this.particlerParticle.setGravity(this.gravity);
        engine.add(this.particlerParticle);
        this.particlerParticle.setRunnabler(
                Runnabler.createClient()
                        .delayRun(this.lifetime, (runnabler) -> this.remove())
        );
        VFXer.activeWrappers.add(this);
        return this;
    }

    @Override
    public void remove() {
        if (this.particlerParticle != null)
            this.particlerParticle.remove();
        VFXer.activeWrappers.remove(this);
    }
}
