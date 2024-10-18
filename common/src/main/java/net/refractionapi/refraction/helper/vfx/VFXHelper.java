package net.refractionapi.refraction.helper.vfx;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.network.protocol.game.ClientboundLevelParticlesPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;

import java.util.Random;

import static net.refractionapi.refraction.helper.vec3.Vec3Helper.calculateViewVector;
import static net.refractionapi.refraction.helper.vec3.Vec3Helper.getDegreesBetweenTwoPoints;

public class VFXHelper {

    static Random random = new Random();


    /**
     * Summon particles around given entity.
     */
    public static void summonParticlesAroundEntity(LivingEntity entity, ParticleOptions particleOptions, ServerLevel level, int particleAmount) {
        for (int i = 0; i < particleAmount; ++i) {
            double d0 = random.nextGaussian() * 0.02D;
            double d1 = random.nextGaussian() * 0.02D;
            double d2 = random.nextGaussian() * 0.02D;
            level.sendParticles(particleOptions, entity.getRandomX(1), entity.getRandomY() + 0.5D, entity.getRandomZ(1), particleAmount, d0, d1, d2, 0.0D);
        }
    }


    public static void summonParticlesAroundEntity(LivingEntity entity, ParticleOptions particleOptions, int particleAmount) {
        if (entity.level() instanceof ServerLevel level)
            summonParticlesAroundEntity(entity, particleOptions, level, particleAmount);
    }

    public static void shootBeamOfParticles(LivingEntity livingEntity, ParticleOptions particleOptions, double range, Vec3 offset) {
        if (livingEntity.level() instanceof ServerLevel serverLevel) {
            Vec3 vec3 = livingEntity.getEyePosition();
            for (int x = 0; x < range; x++) {
                Vec3 vec31 = calculateViewVector(livingEntity.getXRot(), livingEntity.getYRot()).scale(x);
                Vec3 vec32 = vec3.add(vec31);
                if (serverLevel.getBlockState(BlockPos.containing(vec32.x, vec32.y, vec32.z)).isAir()) {
                    sendLongDistanceParticles(serverLevel, particleOptions, vec32.x + offset.x, vec32.y + offset.y, vec32.z + offset.z, 1, 0, 0, 0, 0);
                } else {
                    break;
                }
            }
        }
    }

    /**
     * Creates a line of particles between two points.
     */
    public static void particleLine(ParticleOptions particle, Vec3 pos1, Vec3 pos2, ServerLevel serverLevel) {
        int range = (int) pos1.distanceTo(pos2);
        float[] degrees = getDegreesBetweenTwoPoints(pos1, pos2);
        for (int x = 0; x < range; x++) {
            Vec3 vec3 = calculateViewVector(degrees[0], degrees[1]).scale(-x);
            Vec3 vec31 = pos1.add(vec3);
            sendLongDistanceParticles(serverLevel, particle, vec31.x, vec31.y, vec31.z, 1, 0, 0, 0, 0);
        }
    }


    public static void sendLongDistanceParticles(ServerLevel serverLevel, ParticleOptions pType, double pPosX, double pPosY, double pPosZ, int pParticleCount, double pXOffset, double pYOffset, double pZOffset, double pSpeed) {
        ClientboundLevelParticlesPacket clientboundlevelparticlespacket = new ClientboundLevelParticlesPacket(pType, true, pPosX, pPosY, pPosZ, (float) pXOffset, (float) pYOffset, (float) pZOffset, (float) pSpeed, pParticleCount);
        for (int j = 0; j < serverLevel.players().size(); ++j) {
            ServerPlayer serverplayer = serverLevel.players().get(j);
            BlockPos blockpos = serverplayer.blockPosition();
            if (blockpos.closerToCenterThan(new Vec3(pPosX, pPosY, pPosZ), 512.0D)) {
                serverplayer.connection.send(clientboundlevelparticlespacket);
            }
        }
    }

}
