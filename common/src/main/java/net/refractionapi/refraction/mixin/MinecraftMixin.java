package net.refractionapi.refraction.mixin;

import net.minecraft.client.Minecraft;
import net.refractionapi.refraction.Refraction;
import net.refractionapi.refraction.config.RConfig;
import net.refractionapi.refraction.events.RefractionClientEvents;
import net.refractionapi.refraction.feature.examples.screen.ExampleScreenRegistry;
import net.refractionapi.refraction.gui.RIMGuiInternal;
import net.refractionapi.refraction.util.Keybindings;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Slice;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Minecraft.class)
public class MinecraftMixin {
    @Inject(method = "<init>", at = @At("RETURN"))
    public void init(CallbackInfo ci) {
        Minecraft client = Minecraft.getInstance();
        Refraction.startGui(client.getWindow().getWindow());
    }

    @Inject(method = "runTick", at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/pipeline/RenderTarget;bindWrite(Z)V"), slice = @Slice(
            from = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/systems/RenderSystem;clear(IZ)V", remap = false),
            to = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/GameRenderer;render(Lnet/minecraft/client/DeltaTracker;Z)V")))
    public void beginFrame(CallbackInfo ci) {
        RIMGuiInternal.get().beginFrame();
    }

    @Inject(method = "runTick", at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/platform/Window;updateDisplay()V", shift = At.Shift.BEFORE))
    public void endFrame(CallbackInfo ci) {
        RIMGuiInternal.get().endFrame();
    }

    @Inject(method = "handleKeybinds", at = @At("HEAD"))
    private void handleKeybinds(CallbackInfo ci) {
        if (!RConfig.debugTools || !(Minecraft.getInstance().player != null && Minecraft.getInstance().player.hasPermissions(2))) return;
        while (Keybindings.DEBUG_RENDERERS.mapping().consumeClick()) {
            RIMGuiInternal.get().toggle();
        }
    }

    @Inject(method = "clearLevel(Lnet/minecraft/client/gui/screens/Screen;)V", at = @At("HEAD"))
    public void disconnect(CallbackInfo ci) {
        RefractionClientEvents.CLIENT_PLAYER_LEAVE.invoker().onEvent();
    }
}
