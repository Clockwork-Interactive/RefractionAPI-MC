package net.refractionapi.refraction.mixin;

import net.minecraft.client.gui.screens.worldselection.CreateWorldScreen;
import net.minecraft.world.level.WorldDataConfiguration;
import net.refractionapi.refraction.config.RRuntimeConfig;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(CreateWorldScreen.class)
public class CreateWorldScreenMixin {
    @Inject(method = "openExperimentsScreen", at = @At("HEAD"), cancellable = true)
    public void open(WorldDataConfiguration worldDataConfiguration, CallbackInfo ci) {
        if (RRuntimeConfig.debugTools) ci.cancel();
    }
}
