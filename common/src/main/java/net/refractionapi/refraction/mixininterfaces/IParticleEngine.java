package net.refractionapi.refraction.mixininterfaces;

import net.minecraft.client.particle.ParticleEngine;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;

public interface IParticleEngine {
    <T extends ParticleOptions> void registerParticle(ParticleType<T> particleType, ParticleEngine.SpriteParticleRegistration<T> particleMetaFactory);
}
