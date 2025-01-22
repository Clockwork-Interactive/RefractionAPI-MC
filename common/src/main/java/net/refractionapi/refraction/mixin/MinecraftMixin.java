package net.refractionapi.refraction.mixin;

import net.minecraft.client.Minecraft;
import net.refractionapi.refraction.config.RConfig;
import net.refractionapi.refraction.events.RefractionClientEvents;
import net.refractionapi.refraction.feature.examples.screen.ExampleScreenRegistry;
import net.refractionapi.refraction.util.Keybindings;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Minecraft.class)
public class MinecraftMixin {
    @Inject(method = "handleKeybinds", at = @At("HEAD"))
    private void handleKeybinds(CallbackInfo ci) {
        if (!RConfig.debugTools) return;
        while (Keybindings.DEBUG_RENDERERS.mapping().consumeClick()) {
            ExampleScreenRegistry.DASHBOARD.setScreen();
        }
    }

    @Inject(method = "disconnect(Lnet/minecraft/client/gui/screens/Screen;Z)V", at = @At("HEAD"))
    public void disconnect(CallbackInfo ci) {
        RefractionClientEvents.CLIENT_PLAYER_LEAVE.invoker().onEvent();
    }
}
