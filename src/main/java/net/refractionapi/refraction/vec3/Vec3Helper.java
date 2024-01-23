package net.refractionapi.refraction.vec3;

import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import net.refractionapi.refraction.Refraction;

public class Vec3Helper {

    public static Vec3 getLookAtVec3(LivingEntity livingEntity, double range) {
        Vec3 vec3 = livingEntity.getEyePosition();
        Vec3 vec31 = calculateViewVector(livingEntity.getXRot(), livingEntity.getYRot()).scale(range);
        return vec3.add(vec31);
    }

    public static Vec3 getLookAtVec3NoXRot(LivingEntity livingEntity, double range) {
        Vec3 vec3 = livingEntity.getEyePosition();
        Vec3 vec31 = calculateViewVector(0, livingEntity.getYRot()).scale(range);
        return vec3.add(vec31);
    }

    public static Vec3 getLookAtVec3NoYRot(LivingEntity livingEntity, double range) {
        Vec3 vec3 = livingEntity.getEyePosition();
        Vec3 vec31 = calculateViewVector(livingEntity.getXRot(), 0).scale(range);
        return vec3.add(vec31);
    }

    public static Vec3 calculateViewVector(float pXRot, float pYRot) {
        float f = pXRot * ((float) Math.PI / 180F);
        float f1 = -pYRot * ((float) Math.PI / 180F);
        float f2 = Mth.cos(f1);
        float f3 = Mth.sin(f1);
        float f4 = Mth.cos(f);
        float f5 = Mth.sin(f);
        return new Vec3((f3 * f4), (-f5), (f2 * f4));
    }

    public static Vec3 getVec(Player player, int distance, int offset) {
        Vec3 vec3 = player.getEyePosition();
        Vec3 vec31F = Vec3Helper.calculateViewVector(player.getXRot(), player.getYRot()).scale(distance);
        Vec3 vec31S = Vec3Helper.calculateViewVector(player.getXRot(), player.getYRot() + 90.0F).scale(offset);
        Vec3 FBVector = vec3.add(vec31F);
        Vec3 RLVector = vec3.add(vec31S);
        Vec3 vectorDifference = FBVector.subtract(RLVector);
        return vec3.add(vectorDifference);
    }

    public static void knockback(LivingEntity toKnockback, LivingEntity from, float strength) {
        toKnockback.knockback(strength, Mth.sin(from.getViewYRot(1) * ((float) Math.PI / 180F)), (-Mth.cos(from.getViewXRot(1) * ((float) Math.PI / 180F))));
    }

    /**
     * @return float[0] = xRot; float[1] = yRot;
     */
    public static float[] getDegreesBetweenTwoPoints(Vec3 pos1, Vec3 pos2) {
        double differenceInX = pos1.x - pos2.x;
        double differenceInY = pos1.y - pos2.y;
        double differenceInZ = pos1.z - pos2.z;
        double length = Math.sqrt(differenceInX * differenceInX + differenceInZ * differenceInZ);
        return new float[]{Mth.wrapDegrees((float) (-(Mth.atan2(differenceInY, length) * (double) (180F / (float) Math.PI)))), Mth.wrapDegrees((float) (Mth.atan2(differenceInZ, differenceInX) * (double) (180F / (float) Math.PI)) - 90.0F)};
    }

    /**
     * Calculates if a block position is in a certain angle. <br>
     * To check if an entity is not behind a wall, use {@link LivingEntity#hasLineOfSight(Entity)}
     * Limits: 1 <= angle <= 360;
     */
    public static boolean isInAngle(Entity entity, BlockPos blockPos, double angle) {
        Vec3 dirVec = new Vec3(blockPos.getX() - entity.getX(), blockPos.getY() - entity.getY(), blockPos.getZ() - entity.getZ()).normalize();
        double dot = dirVec.dot(calculateViewVector(entity.getXRot(), entity.getYRot()).normalize());
        return dot >= (Mth.lerp(angle / 360, 1.0F, -1.0F));
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
        return livingEntity.getX() - livingEntity.xOld != 0 || livingEntity.getY() - livingEntity.yOld != 0 || livingEntity.getZ() - livingEntity.zOld != 0;
    }

}
