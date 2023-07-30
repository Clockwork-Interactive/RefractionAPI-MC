package net.refractionapi.refraction.camerashake;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.server.ServerWorld;
import net.refractionapi.refraction.networking.ModMessages;
import net.refractionapi.refraction.networking.S2C.InvokeCameraShakeS2CPacket;

public class CameraShakeHandler {

    public static void invokeCameraShakeToPlayer(int duration, int intensity, ServerPlayerEntity serverPlayer) {
        ModMessages.sendToPlayer(new InvokeCameraShakeS2CPacket(duration, intensity), serverPlayer);
    }

    public static void invokeCameraShakeToPlayersWithinRange(int duration, int intensity, int range, BlockPos pos, ServerWorld level) {
        AxisAlignedBB aabb = new AxisAlignedBB(pos.getX() - range, pos.getY() - range, pos.getZ() - range, pos.getX() + range, pos.getY() + range, pos.getZ() + range);
        for (Entity entity : level.getEntities(null, aabb)) {
            if (entity instanceof ServerPlayerEntity) {
                invokeCameraShakeToPlayer(duration, intensity, (ServerPlayerEntity) entity);
            }
        }
    }

    public static void invokeCameraShakeToPlayersWithinRange(int duration, int intensity, int range, ServerPlayerEntity serverPlayer) {
        AxisAlignedBB aabb = new AxisAlignedBB(serverPlayer.getX() - range, serverPlayer.getY() - range, serverPlayer.getZ() - range, serverPlayer.getX() + range, serverPlayer.getY() + range, serverPlayer.getZ() + range);
        for (Entity entity : serverPlayer.level.getEntities(null, aabb)) {
            if (entity instanceof ServerPlayerEntity) {
                invokeCameraShakeToPlayer(duration, intensity, (ServerPlayerEntity) entity);
            }
        }
    }

}
