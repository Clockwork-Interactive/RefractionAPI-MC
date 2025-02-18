package net.refractionapi.refraction.mixin;

import net.minecraft.server.level.ServerPlayer;
import net.refractionapi.refraction.events.RefractionEvents;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerPlayer.class)
public class ServerPlayerMixin {
    @Inject(at = @At("RETURN"), method = "restoreFrom")
    public void restore(ServerPlayer pThat, boolean pKeepEverything, CallbackInfo ci) {
        RefractionEvents.PLAYER_CLONE.invoker().clone((ServerPlayer) (Object) this, pThat);
    }

    @Inject(at = @At("RETURN"), method = "tick")
    public void tick(CallbackInfo ci) {

    }
}
