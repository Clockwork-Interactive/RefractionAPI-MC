package net.refractionapi.refraction.mixin;

import net.minecraft.network.protocol.game.DebugPackets;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.pathfinder.Path;
import net.refractionapi.refraction.debug.RDebugRenderers;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(DebugPackets.class)
public class DebugPacketsMixin {

    @Inject(method = "sendPathFindingPacket", at = @At("HEAD"))
    private static void send(Level pLevel, Mob pMob, Path pPath, float pMaxDistanceToWaypoint, CallbackInfo ci) {
        if (pMob.level() instanceof ServerLevel serverLevel)
            RDebugRenderers.instance().renderPath(pMob.getId(), pPath, pMaxDistanceToWaypoint, serverLevel);
    }

}
