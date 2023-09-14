package net.refractionapi.refraction.vec3;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.math.vector.Vector3i;

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

    public static boolean isEntityMoving(LivingEntity livingEntity) {
        return livingEntity.xOld - livingEntity.getX() != 0 || livingEntity.yOld - livingEntity.getY() != 0 || livingEntity.zOld - livingEntity.getZ() != 0;
    }

}
