package net.refractionapi.refraction.mixin;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.ParticleEngine;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.refractionapi.refraction.events.RefractionClientEvents;
import net.refractionapi.refraction.mixininterfaces.IParticleEngine;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ParticleEngine.class)
public abstract class ParticleEngineMixin implements IParticleEngine {
    @Shadow protected abstract <T extends ParticleOptions> void register(ParticleType<T> pParticleType, ParticleEngine.SpriteParticleRegistration<T> pParticleMetaFactory);

    @Inject(method = "<init>", at = @At("RETURN"))
    private void onInit(ClientLevel pLevel, TextureManager pTextureManager, CallbackInfo ci) {
        RefractionClientEvents.REGISTER_PARTICLES.invoker().onRegister(this);
    }

    @Override
    public <T extends ParticleOptions> void registerParticle(ParticleType<T> particleType, ParticleEngine.SpriteParticleRegistration<T> particleMetaFactory) {
        this.register(particleType, particleMetaFactory);
    }
}
