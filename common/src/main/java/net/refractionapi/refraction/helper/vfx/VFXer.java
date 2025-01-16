package net.refractionapi.refraction.helper.vfx;

import net.minecraft.world.entity.LivingEntity;
import net.refractionapi.refraction.events.RefractionClientEvents;
import net.refractionapi.refraction.helper.registry.RRegister;
import org.joml.Quaternionf;
import org.joml.Quaternionfc;

import java.util.concurrent.CopyOnWriteArrayList;

public class VFXer {
    protected static final CopyOnWriteArrayList<VFXWrapper<?>> activeWrappers = new CopyOnWriteArrayList<>();

    public Quaternionfc getRotation(LivingEntity entity) {
        return new Quaternionf().lookAlong(entity.getLookAngle().toVector3f().mul(-1.0F), entity.getUpVector(1.0F).toVector3f());
    }

    public ParticleWrapper  wrapParticle(RRegister<DynamicParticleType> particleType) {
        return new ParticleWrapper(this, particleType);
    }

    public VFXRenderer createRenderer() {
        return new VFXRenderer(this);
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    public static void init() {
        RefractionClientEvents.BEFORE_ENTITIES.register(context -> {
            for (VFXWrapper activeWrapper : activeWrappers) {
                if (activeWrapper.updater() != null) {
                    activeWrapper.updater().accept(activeWrapper, context);
                }
            }
        });
    }
}
