package net.refractionapi.refraction.camerashake;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.AABB;
import net.refractionapi.refraction.networking.ModMessages;
import net.refractionapi.refraction.networking.S2C.InvokeCameraShakeS2CPacket;

public class CameraShakeHandler {

    public static void invokeCameraShakeToPlayer(int duration, int intensity, ServerPlayer serverPlayer) {
        ModMessages.sendToPlayer(new InvokeCameraShakeS2CPacket(duration, intensity), serverPlayer);
    }

    public static void invokeCameraShakeToPlayersWithinRange(int duration, int intensity, int range, BlockPos pos, ServerLevel level) {
        AABB aabb = new AABB(pos.getX() - range, pos.getY() - range, pos.getZ() - range, pos.getX() + range, pos.getY() + range, pos.getZ() + range);
        for (Entity entity : level.getEntities(null, aabb)) {
            if (entity instanceof ServerPlayer player) {
                invokeCameraShakeToPlayer(duration, intensity, player);
            }
        }
    }

    public static void invokeCameraShakeToPlayersWithinRange(int duration, int intensity, int range, ServerPlayer serverPlayer) {
        AABB aabb = new AABB(serverPlayer.getX() - range, serverPlayer.getY() - range, serverPlayer.getZ() - range, serverPlayer.getX() + range, serverPlayer.getY() + range, serverPlayer.getZ() + range);
        for (Entity entity : serverPlayer.level().getEntities(null, aabb)) {
            if (entity instanceof ServerPlayer player) {
                invokeCameraShakeToPlayer(duration, intensity, player);
            }
        }
    }

}
