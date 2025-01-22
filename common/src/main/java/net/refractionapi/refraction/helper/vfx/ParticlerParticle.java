package net.refractionapi.refraction.helper.vfx;

import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.*;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.world.phys.Vec3;
import net.refractionapi.refraction.helper.runnable.Runnabler;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

import java.util.function.BiConsumer;

public class ParticlerParticle extends TextureSheetParticle {
    protected ParticleRenderType renderType = ParticleRenderType.PARTICLE_SHEET_TRANSLUCENT;
    protected SpriteSet set;
    protected Vec3 movement = Vec3.ZERO;
    protected BiConsumer<Runnabler, ParticlerParticle> onTick = (runnabler, particle) -> {
    };
    protected Runnabler runnabler = null;

    protected ParticlerParticle(ClientLevel pLevel, double pX, double pY, double pZ, SpriteSet set) {
        super(pLevel, pX, pY, pZ);
        this.setSprite(set);
        this.setSpriteFromAge(set);
    }

    @ApiStatus.Internal
    public void setRunnabler(Runnabler runnabler) {
        this.runnabler = runnabler;
    }

    public float getGravity() {
        return gravity;
    }

    public void setGravity(float gravity) {
        this.gravity = gravity;
    }

    public ParticlerParticle setSprite(SpriteSet set) {
        this.set = set;
        return this;
    }

    public ParticlerParticle setMovement(Vec3 movement) {
        this.movement = movement;
        return this;
    }

    public ParticlerParticle setMovement(double x, double y, double z) {
        this.movement = new Vec3(x, y, z);
        return this;
    }

    public ParticlerParticle setPos(Vec3 vec3) {
        this.x = vec3.x;
        this.y = vec3.y;
        this.z = vec3.z;
        return this;
    }

    public ParticlerParticle onTick(BiConsumer<Runnabler, ParticlerParticle> tickConsumer) {
        this.onTick = tickConsumer;
        return this;
    }

    public Vec3 getMovement() {
        return this.movement;
    }

    public Vec3 getVelocity() {
        return new Vec3(this.x - this.xo, this.y - this.yo, this.z - this.zo);
    }

    public Vec3 position() {
        return new Vec3(this.x, this.y, this.z);
    }

    @Override
    public void render(VertexConsumer pBuffer, Camera pRenderInfo, float pPartialTicks) {
        super.render(pBuffer, pRenderInfo, pPartialTicks);
    }

    @Override
    public void tick() {
        this.onTick.accept(this.runnabler, this);
        this.xo = this.x;
        this.yo = this.y;
        this.zo = this.z;
        if (this.age++ >= this.lifetime) {
            this.remove();
            return;
        }
        this.move(this.movement.x, this.movement.y, this.movement.z);
    }

    @Override
    public ParticleRenderType getRenderType() {
        return renderType;
    }

    public static class Provider implements ParticleProvider<SimpleParticleType> {
        private final SpriteSet set;

        public Provider(SpriteSet set) {
            this.set = set;
        }

        public ParticlerParticle create(ClientLevel pLevel, double pX, double pY, double pZ) {
            return new ParticlerParticle(pLevel, pX, pY, pZ, this.set);
        }

        @Override
        public @Nullable Particle createParticle(SimpleParticleType pType, ClientLevel pLevel, double pX, double pY, double pZ, double pXSpeed, double pYSpeed, double pZSpeed) {
            return new ParticlerParticle(pLevel, pX, pY, pZ, this.set);
        }
    }
}
