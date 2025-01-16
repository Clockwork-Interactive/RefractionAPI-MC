package net.refractionapi.refraction.helper.vfx;

import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.ParticleEngine;
import net.minecraft.world.phys.Vec3;
import net.refractionapi.refraction.helper.registry.RRegister;
import net.refractionapi.refraction.helper.runnable.Runnabler;

import java.util.function.BiConsumer;

public class ParticleWrapper extends VFXWrapper<ParticleWrapper> {
    private final RRegister<DynamicParticleType> particleRegister;
    private BiConsumer<Runnabler, DynamicParticle> onTick = (runnabler, particle) -> {
    };
    private Vec3 spawn = Vec3.ZERO;
    private DynamicParticle dynamicParticle;
    private int lifetime = 0;
    private float gravity = 0.0F;

    public ParticleWrapper(VFXer vfXer, RRegister<DynamicParticleType> particleRegister) {
        super(vfXer);
        this.particleRegister = particleRegister;
    }

    public ParticleWrapper onTick(BiConsumer<Runnabler, DynamicParticle> onTick) {
        this.onTick = onTick;
        return this;
    }

    public ParticleWrapper setSpawn(Vec3 spawn) {
        this.spawn = spawn;
        return this;
    }

    public ParticleWrapper setLifetime(int lifetime) {
        this.lifetime = lifetime;
        return this;
    }

    public ParticleWrapper setGravity(float gravity) {
        this.gravity = gravity;
        return this;
    }

    public ParticleWrapper spawn(Vec3 spawn) {
        this.setSpawn(spawn);
        return this.spawn();
    }

    public DynamicParticle particle() {
        return this.dynamicParticle;
    }

    @Override
    public ParticleWrapper spawn() {
        ParticleEngine engine = Minecraft.getInstance().particleEngine;
        this.dynamicParticle = (DynamicParticle) engine.createParticle(this.particleRegister.get(), this.spawn.x, this.spawn.y, this.spawn.z, 0, 0, 0);
        if (this.dynamicParticle == null) throw new NullPointerException("Failed to create particle %s".formatted(this.particleRegister.getId()));
        this.dynamicParticle.onTick(this.onTick);
        this.dynamicParticle.setLifetime(this.lifetime);
        this.dynamicParticle.setGravity(this.gravity);
        engine.add(this.dynamicParticle);
        this.dynamicParticle.setRunnabler(
                Runnabler.createClient()
                        .delayRun(this.lifetime, (runnabler) -> this.remove())
        );
        VFXer.activeWrappers.add(this);
        return this;
    }

    @Override
    public void remove() {
        if (this.dynamicParticle != null)
            this.dynamicParticle.remove();
        VFXer.activeWrappers.remove(this);
    }
}
