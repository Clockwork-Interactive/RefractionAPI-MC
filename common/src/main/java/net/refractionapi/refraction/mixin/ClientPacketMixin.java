package net.refractionapi.refraction.mixin;

import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.network.protocol.game.ClientboundLoginPacket;
import net.refractionapi.refraction.events.RefractionClientEvents;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPacketListener.class)
public class ClientPacketMixin {
    @Inject(
            method = "handleLogin",
            at = @At("TAIL")
    )
    private void handleLogin(ClientboundLoginPacket pPacket, CallbackInfo ci) {
        RefractionClientEvents.CLIENT_PLAYER_JOIN.invoker().onEvent();
    }
}
