package net.refractionapi.refraction.mixin;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.refractionapi.refraction.data.TData;
import net.refractionapi.refraction.events.RefractionEvents;
import net.refractionapi.refraction.mixininterfaces.IServerPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.IdentityHashMap;

@Mixin(ServerPlayer.class)
public class ServerPlayerMixin implements IServerPlayer {
    @Unique
    final IdentityHashMap<TData, TData> data = new IdentityHashMap<>();

    @Inject(at = @At("RETURN"), method = "restoreFrom")
    public void restore(ServerPlayer pThat, boolean pKeepEverything, CallbackInfo ci) {
        RefractionEvents.PLAYER_CLONE.invoker().clone((ServerPlayer) (Object) this, pThat);
    }

    @Inject(at = @At("RETURN"), method = "tick")
    public void tick(CallbackInfo ci) {
    }

    @Override
    public TData get(TData data) {
        return this.data.computeIfAbsent(data, data1 -> data.create(data.id, (Player) (Object) this));
    }
}
