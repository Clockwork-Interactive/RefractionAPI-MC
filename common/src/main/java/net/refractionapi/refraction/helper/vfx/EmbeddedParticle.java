package net.refractionapi.refraction.helper.vfx;

import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.*;

public class EmbeddedParticle extends TextureSheetParticle {
    private final SpriteSet sprites;
    private int frequency = 5;
    private float particleSIze = 1.0F;
    private EmbeddedParticleOptions options;

    protected EmbeddedParticle(ClientLevel pLevel, double pX, double pY, double pZ, double pXSpeed, double pYSpeed, double pZSpeed, float v, SpriteSet set) {
        super(pLevel, pX, pY, pZ, pXSpeed, pYSpeed, pZSpeed);
        this.sprites = set;
        this.friction = 0.8F;
        this.quadSize = particleSIze;
        this.setSpriteFromAge(set);

        this.rCol = 1f;
        this.gCol = 1f;
        this.bCol = 1f;
    }

    public void tick() {
        if (this.age++ >= this.lifetime) {
            this.remove();
        } else {
            this.setSpriteFromAge(this.sprites);
            if (this.options.tickConsumer == null) {
                this.remove();
                return;
            }
            this.options.tickConsumer.accept(this);
        }
    }

    @Override
    public void setSpriteFromAge(SpriteSet sprite) {
        if (!this.removed) {
            this.sprite = this.age % this.frequency == 0 ? this.sprites.get(this.random) : this.sprite;
        }
    }

    @Override
    public void render(VertexConsumer buffer, Camera renderInfo, float partialTicks) {
        if (this.options.tickConsumer == null) {
            this.remove();
            return;
        }
        this.options.renderConsumer.accept(this, partialTicks);
        super.render(buffer, renderInfo, partialTicks);
    }

    @Override
    public ParticleRenderType getRenderType() {
        return ParticleRenderType.PARTICLE_SHEET_TRANSLUCENT;
    }

    public static class Provider implements ParticleProvider<EmbeddedParticleOptions> {
        private final SpriteSet sprites;
        private int lifetime = 10;
        private float gravity = 0.0F;
        private int frequency = 5;
        private float particleSize = 1.0F;

        public Provider(SpriteSet pSprites) {
            this.sprites = pSprites;
        }

        public Provider setLifetime(int lifetime) {
            this.lifetime = lifetime;
            return this;
        }

        public Provider setGravity(float gravity) {
            this.gravity = gravity;
            return this;
        }

        public Provider setFrequency(int frequency) {
            this.frequency = frequency;
            return this;
        }

        public Provider setSize(float size) {
            this.particleSize = size;
            return this;
        }

        public Particle createParticle(EmbeddedParticleOptions pType, ClientLevel pLevel, double pX, double pY, double pZ, double pXSpeed, double pYSpeed, double pZSpeed) {
            EmbeddedParticle particle = new EmbeddedParticle(pLevel, pX, pY, pZ, pXSpeed, pYSpeed, pZSpeed, 1.0F, this.sprites);
            particle.gravity = gravity;
            particle.lifetime = lifetime;
            particle.frequency = frequency;
            particle.particleSIze = particleSize;
            particle.options = pType;
            return particle;
        }
    }
}
