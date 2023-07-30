package net.refractionapi.refraction.mixin;

import net.minecraft.client.renderer.ActiveRenderInfo;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.IBlockReader;
import net.refractionapi.refraction.mixininterfaces.ICameraMixin;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ActiveRenderInfo.class)
public abstract class CameraMixin implements ICameraMixin {

    /* //Currently Unused as current equations doesn't require them, but can be introduced later for more creative control
    private static final int FREQUENCY = 60;
    private static final int AMPLITUDE = 10; //How much it can move in pixels (from center)
    */
    private int shakeStartTick = 0;
    private int shakeDurationTick = 0;
    private int intensity = 10; //Value clamped from 0-10 to change intensity of shake

    //REMEMBER, this isn't a "tick", this runs Every Frame
    @Inject(at = @At("TAIL"), method = "setup")
    void setup(IBlockReader blockReader, Entity pEntity, boolean p_216772_3_, boolean p_216772_4_, float p_216772_5_, CallbackInfo ci) {
        if (shakeDurationTick != 0) { //Fixes Brick at game startup
            double normalisedIntensity = intensity / 10.0D;
            double cameraShakeMultiplier = normalisedIntensity;
            //Should use decay equation
            if (shakeDurationTick <= shakeStartTick * 0.8D) {
                cameraShakeMultiplier = normalisedIntensity * shakeDurationTick / shakeStartTick * 0.8D;
            }

            double camX = (0.25 - pEntity.level.random.nextDouble() / 2) * cameraShakeMultiplier;
            double camY = (0.25 - pEntity.level.random.nextDouble() / 2) * cameraShakeMultiplier;
            move(0, camY, camX);
        }
    }

    @Inject(at = @At("TAIL"), method = "tick")
    void tick(CallbackInfo ci) {
        if (shakeDurationTick > 0) {
            shakeDurationTick--;
        }
    }

    /**
     * Causes a camera shake. Starts linearly decaying after 8/10's of the way through the shake
     *
     * @param durationInTicks The duration of the shake in ticks
     * @param intensity       Clamped 0 - 10 that controls how intense the shake is
     */
    @Override
    public void startCameraShake(int durationInTicks, int intensity) {
        this.shakeStartTick = durationInTicks;
        this.shakeDurationTick = durationInTicks;
        this.intensity = MathHelper.clamp(intensity, 0, 10);
    }

    @Shadow
    protected abstract void move(double pDistanceOffset, double pVerticalOffset, double pHorizontalOffset);
}