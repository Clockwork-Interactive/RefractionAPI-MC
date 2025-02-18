package net.refractionapi.refraction.mixin;

import net.minecraft.client.MouseHandler;
import net.refractionapi.refraction.gui.RIMGuiInternal;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MouseHandler.class)
public class MouseHandlerMixin {
    @Inject(method = "onPress", at = @At("HEAD"), cancellable = true)
    public void onPress(long window, int button, int action, int mods, CallbackInfo ci) {
        if (RIMGuiInternal.get().onMouseButton(window, button, action, mods)) {
            ci.cancel();
        }
    }

    @Inject(method = "onScroll", at = @At("HEAD"), cancellable = true)
    public void onScroll(long window, double xOffset, double yOffset, CallbackInfo ci) {
        if (RIMGuiInternal.get().onScroll(xOffset, yOffset)) {
            ci.cancel();
        }
    }

    @Inject(method = "grabMouse", at = @At("HEAD"))
    public void grabMouse(CallbackInfo ci) {
        RIMGuiInternal.get().onGrabMouse();
    }
}
