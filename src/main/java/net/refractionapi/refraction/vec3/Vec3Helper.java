package net.refractionapi.refraction.vec3;

import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;

public class Vec3Helper {

    public static Vector3d lerp(Vector3d from, Vector3d to, double delta) {
        return new Vector3d(MathHelper.lerp(delta, from.x, to.x), MathHelper.lerp(delta, from.y, to.y), MathHelper.lerp(delta, from.z, to.z));
    }

    public static Vector3d getLookAtVec3(LivingEntity livingEntity, double range) {
        Vector3d vec3 = livingEntity.getEyePosition(0);
        Vector3d vec31 = livingEntity.getViewVector(1);
        return vec3.add(vec31);
    }

    public static Vector3d getLookAtVec3NoXRot(LivingEntity livingEntity, double range) {
        Vector3d vec3 = livingEntity.getEyePosition(0);
        Vector3d vec31 = calculateViewVector(0, livingEntity.getViewYRot(1)).scale(range);
        return vec3.add(vec31);
    }

    public static Vector3d getLookAtVec3NoYRot(LivingEntity livingEntity, double range) {
        Vector3d vec3 = livingEntity.getEyePosition(0);
        Vector3d vec31 = calculateViewVector(livingEntity.getViewXRot(1), 0).scale(range);
        return vec3.add(vec31);
    }

    public static Vector3d calculateViewVector(float pXRot, float pYRot) {
        float f = pXRot * ((float) Math.PI / 180F);
        float f1 = -pYRot * ((float) Math.PI / 180F);
        float f2 = MathHelper.cos(f1);
        float f3 = MathHelper.sin(f1);
        float f4 = MathHelper.cos(f);
        float f5 = MathHelper.sin(f);
        return new Vector3d((f3 * f4), (-f5), (f2 * f4));
    }

    public static void knockback(LivingEntity toKnockback, LivingEntity from, float strength) {
        toKnockback.knockback(strength, MathHelper.sin(from.getViewYRot(1) * ((float) Math.PI / 180F)), (-MathHelper.cos(from.getViewXRot(1) * ((float) Math.PI / 180F))));
    }

    /**
     * Calculates if a block position is in a certain angle. <br>
     * To check if an entity is not behind a wall, use {@link LivingEntity#canSee(Entity)}
     * Limits: 1 <= angle <= 360;
     */
    public static boolean isInAngle(PlayerEntity player, BlockPos blockPos, double angle) {
        Vector3d dirVec = new Vector3d(blockPos.getX() - player.getX(), blockPos.getY() - player.getY(), blockPos.getZ() - player.getZ()).normalize();
        double dot = dirVec.dot(calculateViewVector(player.getViewXRot(1), player.getViewYRot(1)).normalize());
        return dot >= (MathHelper.lerp(angle / 360, 1.0F, -1.0F));
    }

    /**
     * Determines if the entity is moving. <br>
     * Not reliable on clients, use {@link Vec3Helper#isEntityMovingClient(LivingEntity)}.
     *
     * @param livingEntity Entity to check.
     * @return If moving.
     */
    public static boolean isEntityMoving(LivingEntity livingEntity) {
        return livingEntity.walkDist - livingEntity.walkDistO > 0;
    }

    /**
     * Use this only on the server! <br>
     * Gets the movement difference for an entity from previous to current tick. <br>
     * Value for player sprint is >= 0.15
     *
     * @param livingEntity Entity to get difference for.
     * @return Movement difference.
     */
    public static float getMovingDifference(LivingEntity livingEntity) {
        return livingEntity.walkDist - livingEntity.walkDistO;
    }

    /**
     * Use this only on the client! <br>
     * Else use {@link Vec3Helper#isEntityMoving(LivingEntity)}
     *
     * @param livingEntity Entity to check.
     * @return Is moving.
     */
    public static boolean isEntityMovingClient(LivingEntity livingEntity) {
        return livingEntity.getX() - livingEntity.xOld > 0 || livingEntity.getY() - livingEntity.yOld > 0 || livingEntity.getZ() - livingEntity.zOld > 0;
    }

}
