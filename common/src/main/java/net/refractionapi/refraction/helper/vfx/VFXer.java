package net.refractionapi.refraction.helper.vfx;

import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.LivingEntity;
import net.refractionapi.refraction.events.RefractionClientEvents;
import net.refractionapi.refraction.helper.registry.RRegister;
import org.joml.Quaternionf;
import org.joml.Quaternionfc;

import java.util.concurrent.CopyOnWriteArrayList;

public class VFXer {
    protected static final CopyOnWriteArrayList<VFXWrapper<?>> activeWrappers = new CopyOnWriteArrayList<>();

    public DeltaTracker deltaTracker() {
        return Minecraft.getInstance().getTimer();
    }

    public Quaternionfc getRotation(LivingEntity entity) {
        return new Quaternionf().lookAlong(entity.getLookAngle().toVector3f().mul(-1.0F), entity.getUpVector(deltaTracker().getGameTimeDeltaTicks()).toVector3f());
    }

    public Particler particler(RRegister<ParticlerType> particleType) {
        return new Particler(this, particleType);
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
