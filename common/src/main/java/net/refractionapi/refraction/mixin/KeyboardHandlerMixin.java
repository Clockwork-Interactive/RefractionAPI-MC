package net.refractionapi.refraction.mixin;

import net.minecraft.client.KeyboardHandler;
import net.refractionapi.refraction.events.RefractionClientEvents;
import net.refractionapi.refraction.gui.RIMGuiInternal;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(KeyboardHandler.class)
public class KeyboardHandlerMixin {
    @Inject(at = @At("TAIL"), method = "keyPress")
    public void keyPressTail(long pWindowPointer, int pKey, int pScanCode, int pAction, int pModifiers, CallbackInfo ci) {
        RefractionClientEvents.KEY_INPUT.invoker().onKeyInput(pKey, pScanCode, pAction, pModifiers);
    }

    @Inject(method = "keyPress", at = @At("HEAD"), cancellable = true)
    public void keyPress(long window, int key, int scancode, int action, int mods, CallbackInfo ci) {
        if (RIMGuiInternal.get().onKey(window, key, scancode, action, mods)) {
            ci.cancel();
        }
    }

    @Inject(method = "charTyped", at = @At("HEAD"), cancellable = true)
    public void charTyped(long window, int codepoint, int mods, CallbackInfo ci) {
        if (RIMGuiInternal.get().onChar(window, codepoint, mods)) {
            ci.cancel();
        }
    }
}
