package net.refractionapi.refraction.mixin;

import net.minecraft.client.gui.screens.Screen;
import net.refractionapi.refraction.client.ClientData;
import net.refractionapi.refraction.client.RefractionClient;
import net.refractionapi.refraction.feature.scheme.RScreen;
import net.refractionapi.refraction.feature.screen.RefractionScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Screen.class)
public class ScreenMixin {
    @Inject(at = @At("HEAD"), method = "onClose", cancellable = true)
    public void close(CallbackInfo ci) {
        if (this instanceof RefractionScreen screen) {
            ClientData.screenHandler.onClose(screen, false);
            if (!screen.screenClosingAuthority())
                ci.cancel();
        }
        if (this instanceof RScreen screen) {
            RefractionClient.screenRegistry.close(false);
        }
    }
}
