package net.refractionapi.refraction.vfx;

import net.minecraft.block.Blocks;
import net.minecraft.client.particle.Particle;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.particles.IParticleData;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.server.ServerWorld;

import java.util.Random;
import java.util.Vector;

import static net.refractionapi.refraction.vec3.Vec3Helper.calculateViewVector;

public class VFXHelper {

    static Random random = new Random();

    /**
     * Adds glint without an enchantment.
     *
     * @param stack Stack to add glint to.
     */
    public static void addGlint(ItemStack stack) {
        CompoundNBT tag = stack.getOrCreateTag();
        tag.put("Enchantments", new ListNBT());
        ListNBT listtag = tag.getList("Enchantments", 10);
        listtag.add(new CompoundNBT());
    }


    /**
     * Summon particles around given entity.
     */
    public static void summonParticlesAroundEntity(Entity entity, IParticleData particleOptions, ServerWorld level, int ParticleAmount) {
        for (int i = 0; i < ParticleAmount; ++i) {
            double d0 = random.nextGaussian() * 0.02D;
            double d1 = random.nextGaussian() * 0.02D;
            double d2 = random.nextGaussian() * 0.02D;
            level.sendParticles(particleOptions, entity.getRandomX(1), entity.getRandomY() + 0.5D, entity.getRandomZ(1), ParticleAmount, d0, d1, d2, 0.0D);
        }
    }

    public static void shootBeamOfParticles(LivingEntity livingEntity, IParticleData particleOptions, double range, double yOffset) {
        if (livingEntity.level instanceof ServerWorld) {
            ServerWorld serverLevel = (ServerWorld) livingEntity.level;
            Vector3d vec3 = livingEntity.getEyePosition(1);

            for (int x = 0; x < range; x++) {
                Vector3d vec31 = calculateViewVector(livingEntity.getViewXRot(1), livingEntity.getViewYRot(1)).scale(x);
                Vector3d vec32 = vec3.add(vec31);
                if (serverLevel.getBlockState(new BlockPos(vec32)).is(Blocks.AIR)) {
                    serverLevel.sendParticles(particleOptions, vec32.x, vec32.y, vec32.z + yOffset, 1, 0, 0, 0, 0);
                } else {
                    break;
                }
            }

        }
    }


}
