package net.refractionapi.refraction.vec3;

import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.refractionapi.refraction.math.EasingFunctions;
import net.refractionapi.refraction.runnable.RunnableHandler;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class Vec3Helper {

    private static final RandomSource random = RandomSource.create();

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

    public static Vec3 getVec(LivingEntity livingEntity, int distance, int offset) {
        Vec3 vec3 = livingEntity.getEyePosition();
        Vec3 vec31F = Vec3Helper.calculateViewVector(livingEntity.getXRot(), livingEntity.getYRot()).scale(distance);
        Vec3 vec31S = Vec3Helper.calculateViewVector(livingEntity.getXRot(), livingEntity.getYRot() + 90.0F).scale(offset);
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
     * Calculates if a vec3 is in a certain angle. <br>
     * Limits: 1 &lt;= angle &lt;= 360;
     */
    public static boolean isInAngle(Vec3 from, Vec3 to, float xRot, float yRot, double angle) {
        Vec3 dirVec = new Vec3(to.x - from.x, to.y - from.y, to.z - from.z).normalize();
        double dot = dirVec.dot(calculateViewVector(xRot, yRot).normalize());
        return dot >= (Mth.lerp(angle / 360, 1.0F, -1.0F));
    }

    /**
     * To check if an entity is not behind a wall, use {@link LivingEntity#hasLineOfSight(Entity)}
     */
    public static boolean isInAngle(Entity entity, Vec3 vec3, double angle) {
        return isInAngle(entity.position(), vec3, entity.getXRot(), entity.getYRot(), angle);
    }

    public static boolean isInAngle(Entity entity, BlockPos pos, double angle) {
        return isInAngle(entity, pos.getCenter(), angle);
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

    /**
     * Smoothly lerp an entity from start to end over a given amount of ticks.
     */
    public static void smoothLerp(Entity entity, Vec3 start, Vec3 end, int ticks, EasingFunctions easingFunction) {
        AtomicInteger tick = new AtomicInteger(0);
        RunnableHandler.addRunnable(() -> {
            tick.getAndIncrement();
            float lerpTicks = (float) tick.get() / ticks;
            lerpTicks = easingFunction.getEasing(lerpTicks);
            Vec3 lerp = start.lerp(end, lerpTicks);
            Vec3 currentPos = entity.position();
            Vec3 delta = lerp.subtract(currentPos);
            entity.setDeltaMovement(delta);
            entity.hurtMarked = true;
            entity.setPosRaw(lerp.x, lerp.y, lerp.z);
        }, ticks);
    }

    public static Vec3 getVec(LivingEntity livingEntity, float distance, float offset) {
        return getVec(livingEntity, livingEntity.getXRot(), livingEntity.getYRot(), distance, offset);
    }

    public static Vec3 getVec(LivingEntity livingEntity, float distance, float offset, float yOffset) {
        return getVec(livingEntity, livingEntity.getXRot(), livingEntity.getYRot(), distance, offset, yOffset);
    }

    public static Vec3 getVecNoXRot(LivingEntity livingEntity, float distance, float offset, float yOffset) {
        return getVec(livingEntity, 0, livingEntity.getYRot(), distance, offset, yOffset);
    }

    public static Vec3 getVec(LivingEntity livingEntity, float xRot, float yRot, float distance, float offset) {
        return getVec(livingEntity, xRot, yRot, distance, offset, 0);
    }

    public static Vec3 getVec(LivingEntity livingEntity, float xRot, float yRot, float distance, float offset, float yOffset) {
        Vec3 vec3 = livingEntity.getEyePosition();
        Vec3 vec31F = Vec3Helper.calculateViewVector(xRot, yRot).scale(distance);
        Vec3 vec31S = Vec3Helper.calculateViewVector(xRot, yRot + 90.0F).scale(offset);
        Vec3 FBVector = vec3.add(vec31F);
        Vec3 RLVector = vec3.add(vec31S);
        Vec3 vectorDifference = FBVector.subtract(RLVector);
        return vec3.add(vectorDifference).add(0, yOffset, 0);
    }

    public static Vec3[] getInbetween(Vec3 vec1, Vec3 vec2) {
        return getInbetween(vec1, vec2, (int) vec1.distanceTo(vec2));
    }

    public static Vec3[] getInbetween(Vec3 vec1, Vec3 vec2, int steps) {
        Vec3[] inbetween = new Vec3[steps];
        for (int i = 0; i < steps; i++) {
            inbetween[i] = vec1.add(vec2.subtract(vec1).scale((float) i / steps));
        }
        return inbetween;
    }

    public static Vec3 getRandomSpherePos(Vec3 origin, float radius) {
        double theta = random.nextDouble() * 2 * Math.PI;
        double phi = Math.acos(2 * random.nextDouble() - 1);
        double r = radius * Math.cbrt(random.nextDouble());

        double x = r * Mth.sin((float) phi) * Mth.cos((float) theta);
        double y = r * Mth.sin((float) phi) * Mth.sin((float) theta);
        double z = r * Mth.cos((float) phi);

        return new Vec3(origin.x + x, origin.y + y, origin.z + z);
    }

    public static BlockPos findSolid(Level level, BlockPos start, int y) {
        BlockPos.MutableBlockPos pos = start.atY(y).mutable();
        while (level.getBlockState(pos).isAir() && pos.getY() > level.getMinBuildHeight()) {
            pos.move(0, -1, 0);
        }
        return pos;
    }

    public static BlockPos findSolid(Level level, BlockPos start) {
        return findSolid(level, start, level.getMaxBuildHeight());
    }

    public static BlockPos findSolid(Level level, Vec3 vec3, float y) {
        return findSolid(level, BlockPos.containing(vec3), (int) y);
    }

    public static BlockPos findSolid(Level level, Vec3 vec3) {
        return findSolid(level, vec3, (float) vec3.y);
    }

    public static List<BlockPos> createSphere(BlockPos center, int radius) {
        final List<BlockPos> ret = new ArrayList<>();
        int centerInt = radius / 2, squareDistance;

        for (int x1 = centerInt - radius; x1 < centerInt + radius; x1++) {
            for (int y1 = centerInt - radius; y1 < centerInt + radius; y1++) {
                for (int z1 = centerInt - radius; z1 < centerInt + radius; z1++) {
                    squareDistance = (x1 - centerInt) * (x1 - centerInt) + (y1 - centerInt) * (y1 - centerInt) + (z1 - centerInt) * (z1 - centerInt);
                    if (squareDistance <= (radius) * (radius))
                        ret.add(new BlockPos(center.getX() + x1 - centerInt, center.getY() + y1, center.getZ() + z1 - centerInt));
                }
            }
        }

        return ret;
    }

    public static List<BlockPos> createHollowSphere(BlockPos center, int radius) {
        final List<BlockPos> ret = new ArrayList<>();
        int centerInt = radius / 2, squareDistance;

        for (int x1 = centerInt - radius; x1 < centerInt + radius; x1++) {
            for (int y1 = centerInt - radius; y1 < centerInt + radius; y1++) {
                for (int z1 = centerInt - radius; z1 < centerInt + radius; z1++) {
                    squareDistance = (x1 - centerInt) * (x1 - centerInt) + (y1 - centerInt) * (y1 - centerInt) + (z1 - centerInt) * (z1 - centerInt);
                    if (squareDistance <= (radius) * (radius) && squareDistance >= (radius - 1) * (radius - 1))
                        ret.add(new BlockPos(center.getX() + x1 - centerInt, center.getY() + y1, center.getZ() + z1 - centerInt));
                }
            }
        }

        return ret;
    }

    public static List<BlockPos> circle(BlockPos center, int radius) {
        final List<BlockPos> ret = new ArrayList<>();
        int centerInt = radius / 2, squareDistance;

        for (int x1 = centerInt - radius; x1 < centerInt + radius; x1++) {
            for (int z1 = centerInt - radius; z1 < centerInt + radius; z1++) {
                squareDistance = (x1 - centerInt) * (x1 - centerInt) + (z1 - centerInt) * (z1 - centerInt);
                if (squareDistance <= (radius) * (radius))
                    ret.add(new BlockPos(center.getX() + x1 - centerInt, center.getY(), center.getZ() + z1 - centerInt));
            }
        }

        return ret;
    }

}
