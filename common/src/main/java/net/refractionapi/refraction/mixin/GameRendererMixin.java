package net.refractionapi.refraction.mixin;

import net.minecraft.client.Camera;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.PostChain;
import net.refractionapi.refraction.client.ClientData;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(GameRenderer.class)
public class GameRendererMixin {
    @Shadow
    private @Nullable PostChain postEffect;

    @Shadow
    private boolean effectActive;

    @Inject(method = "getFov", at = @At("HEAD"), cancellable = true)
    public void getFov(Camera $$0, float $$1, boolean $$2, CallbackInfoReturnable<Double> cir) {
        if (ClientData.currentFOV != -1) {
            cir.setReturnValue(ClientData.currentFOV);
        }
    }

    @Inject(method = "togglePostEffect", at = @At("HEAD"), cancellable = true)
    public void toggle(CallbackInfo ci) {
        if (postEffect != null && !ClientData.isToggleable(postEffect.getName())) {
            effectActive = true;
            ci.cancel();
        }
    }
}
