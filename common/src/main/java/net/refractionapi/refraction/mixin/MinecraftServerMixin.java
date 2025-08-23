package net.refractionapi.refraction.mixin;

import net.minecraft.server.MinecraftServer;
import net.refractionapi.refraction.events.RefractionEvents;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(MinecraftServer.class)
public class MinecraftServerMixin {
    @Inject(
        method = "runServer",
        at = @At(value = "INVOKE_ASSIGN", target = "Lnet/minecraft/Util;getNanos()J", ordinal = 0)
    )
    public void init(CallbackInfo ci) {
        RefractionEvents.SERVER_STARTING.invoker().onEvent((MinecraftServer) (Object) this);
    }

    @Inject(
            method = "saveAllChunks",
            at = @At("TAIL")
    )
    public void saveAll(boolean suppressLog, boolean flush, boolean forced, CallbackInfoReturnable<Boolean> cir) {
        RefractionEvents.ON_SAVE.invoker().onEvent((MinecraftServer) (Object) this);
    }
}
