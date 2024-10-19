package net.refractionapi.refraction.mixin;

import net.minecraft.client.Minecraft;
import net.refractionapi.refraction.Refraction;
import net.refractionapi.refraction.client.screen.DebugRendererScreen;
import net.refractionapi.refraction.util.Keybindings;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Minecraft.class)
public class MinecraftMixin {

    @Inject(method = "handleKeybinds", at = @At("HEAD"))
    private void handleKeybinds(CallbackInfo ci) {
        if (!Refraction.debugTools) return;
        while (Keybindings.DEBUG_RENDERERS.mapping().consumeClick()) {
            Minecraft.getInstance().setScreen(new DebugRendererScreen());
        }
    }

}
