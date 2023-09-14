package net.refractionapi.refraction.vec3;

import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;

public class Vec3Helper {

    public static Vec3 lerp(Vec3 from, Vec3 to, double delta) {
        return new Vec3(Mth.lerp(delta, from.x, to.x), Mth.lerp(delta, from.y, to.y), Mth.lerp(delta, from.z, to.z));
    }

    public static Vec3 getLookAtVec3(LivingEntity livingEntity, double range) {
        Vec3 vec3 = livingEntity.getEyePosition(0);
        Vec3 vec31 = livingEntity.getViewVector(1);
        return vec3.add(vec31);
    }

    public static Vec3 getLookAtVec3NoXRot(LivingEntity livingEntity, double range) {
        Vec3 vec3 = livingEntity.getEyePosition(0);
        Vec3 vec31 = calculateViewVector(0, livingEntity.getViewYRot(1)).scale(range);
        return vec3.add(vec31);
    }

    public static Vec3 getLookAtVec3NoYRot(LivingEntity livingEntity, double range) {
        Vec3 vec3 = livingEntity.getEyePosition(0);
        Vec3 vec31 = calculateViewVector(livingEntity.getViewXRot(1), 0).scale(range);
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

    public static void knockback(LivingEntity toKnockback, LivingEntity from, float strength) {
        toKnockback.knockback(strength, Mth.sin(from.getViewYRot(1) * ((float) Math.PI / 180F)), (-Mth.cos(from.getViewXRot(1) * ((float) Math.PI / 180F))));
    }
    
    /**
     * @return float[0] = xRot; float[1] = yRot;
     */
    public static float[] getDegreesBetweenTwoPoints(BlockPos pos1, BlockPos pos2) {
        double differenceInX = pos1.getX() - pos2.getX();
        double differenceInY = pos1.getY() - pos2.getY();
        double differenceInZ = pos1.getZ() - pos2.getZ();
        double length = Math.sqrt(differenceInX * differenceInX + differenceInZ * differenceInZ);
        return new float[]{Mth.wrapDegrees((float) (-(Mth.atan2(differenceInY, length) * (double) (180F / (float) Math.PI)))), Mth.wrapDegrees((float) (Mth.atan2(differenceInZ, differenceInX) * (double) (180F / (float) Math.PI)) - 90.0F)};
    }

}
