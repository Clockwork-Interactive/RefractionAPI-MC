package net.refractionapi.refraction.mixin;

import net.minecraft.client.Camera;
import net.minecraft.client.renderer.GameRenderer;
import net.refractionapi.refraction.client.ClientData;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(GameRenderer.class)
public class GameRendererMixin {

    @Inject(method = "getFov", at = @At("HEAD"), cancellable = true)
    public void getFov(Camera $$0, float $$1, boolean $$2, CallbackInfoReturnable<Double> cir) {
        if (ClientData.currentFOV != -1) {
            cir.setReturnValue(ClientData.currentFOV);
        }
    }

}
