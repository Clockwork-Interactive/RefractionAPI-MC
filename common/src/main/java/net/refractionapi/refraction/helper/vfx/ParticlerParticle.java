package net.refractionapi.refraction.helper.vfx;

import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.*;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import net.refractionapi.refraction.helper.runnable.Runnabler;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import java.util.function.BiConsumer;

public class ParticlerParticle extends TextureSheetParticle {
    protected ParticleRenderType renderType = ParticleRenderType.PARTICLE_SHEET_TRANSLUCENT;
    protected SpriteSet set;
    protected Vec3 movement = Vec3.ZERO;
    protected BiConsumer<Runnabler, ParticlerParticle> onTick = (runnabler, particle) -> {
    };
    protected Runnabler runnabler = null;
    protected Particler.FloatSetting transparency;
    protected Particler.ColorSetting color;
    protected Particler.FloatSetting scale;
    protected Particler.RotationSetting[] rotations = new Particler.RotationSetting[0];

    protected ParticlerParticle(ClientLevel pLevel, double pX, double pY, double pZ, SpriteSet set) {
        super(pLevel, pX, pY, pZ);
        this.setSprite(set);
        this.setSpriteFromAge(set);
    }

    public float getDelta(float deltaTime) {
        return Mth.clamp((this.age + deltaTime) / (float) this.lifetime, 0.0F, 1.0F);
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
    public Particle scale(float scale) {
        this.quadSize = scale;
        this.setSize(0.2f * scale, 0.2f * scale);
        return this;
    }

    @Override
    public void render(VertexConsumer pBuffer, Camera pRenderInfo, float pPartialTicks) {
        if (this.scale != null) this.scale(this.scale.get(this.getDelta(pPartialTicks)));
        if (this.transparency != null) this.alpha = this.transparency.get(this.getDelta(pPartialTicks));
        if (this.color != null) {
            int color = this.color.get(this.getDelta(pPartialTicks));
            this.rCol = (color >> 16 & 255) / 255.0F;
            this.gCol = (color >> 8 & 255) / 255.0F;
            this.bCol = (color & 255) / 255.0F;
        }
        super.render(pBuffer, pRenderInfo, pPartialTicks);
    }

    @Override
    protected void renderRotatedQuad(VertexConsumer pBuffer, Quaternionf pQuaternion, float pX, float pY, float pZ, float pPartialTicks) {
        float f = this.getQuadSize(pPartialTicks);
        float f1 = this.getU0();
        float f2 = this.getU1();
        float f3 = this.getV0();
        float f4 = this.getV1();
        int i = this.getLightColor(pPartialTicks);
        // since the particle can be rotated, we're rendering both faces --Zeus
        this.renderVertex(pBuffer, pQuaternion, pX, pY, pZ, 1.0F, -1.0F, f, f2, f4, i, pPartialTicks);
        this.renderVertex(pBuffer, pQuaternion, pX, pY, pZ, 1.0F, 1.0F, f, f2, f3, i, pPartialTicks);
        this.renderVertex(pBuffer, pQuaternion, pX, pY, pZ, -1.0F, 1.0F, f, f1, f3, i, pPartialTicks);
        this.renderVertex(pBuffer, pQuaternion, pX, pY, pZ, -1.0F, -1.0F, f, f1, f4, i, pPartialTicks);

        this.renderVertex(pBuffer, pQuaternion, pX, pY, pZ, -1.0F, -1.0F, f, f1, f4, i, pPartialTicks);
        this.renderVertex(pBuffer, pQuaternion, pX, pY, pZ, -1.0F, 1.0F, f, f1, f3, i, pPartialTicks);
        this.renderVertex(pBuffer, pQuaternion, pX, pY, pZ, 1.0F, 1.0F, f, f2, f3, i, pPartialTicks);
        this.renderVertex(pBuffer, pQuaternion, pX, pY, pZ, 1.0F, -1.0F, f, f2, f4, i, pPartialTicks);
    }

    private void renderVertex(VertexConsumer pBuffer, Quaternionf pQuaternion, float pX, float pY, float pZ, float pXOffset, float pYOffset, float pQuadSize, float pU, float pV, int pPackedLight, float partial) {
        Vector3f vector3f = (new Vector3f(pXOffset, pYOffset, 0.0F));
        for (Particler.RotationSetting rotation : this.rotations) {
            vector3f.rotate(rotation.getAxis().rotationDegrees(rotation.get(this.getDelta(partial))));
        }
        vector3f.rotate(pQuaternion).mul(pQuadSize).add(pX, pY, pZ);
        pBuffer.addVertex(vector3f.x(), vector3f.y(), vector3f.z()).setUv(pU, pV).setColor(this.rCol, this.gCol, this.bCol, this.alpha).setLight(pPackedLight);
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
