package net.refractionapi.refraction.cutscenes.point;

import net.minecraft.commands.arguments.EntityAnchorArgument;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;
import net.refractionapi.refraction.cutscenes.Cutscene;
import net.refractionapi.refraction.math.EasingFunctions;

import java.util.ArrayList;
import java.util.List;

import static net.refractionapi.refraction.cutscenes.CutsceneHandler.QUEUE;
import static net.refractionapi.refraction.vec3.Vec3Helper.calculateViewVector;

public class PointHandler {

    public final Cutscene cutscene;
    protected final List<CutscenePoint> points = new ArrayList<>();
    protected int transitionTime;
    protected int lockedTime;
    public Vec3 target;

    public PointHandler(Cutscene cutscene, int transitionTime, int lockedTime) {
        this.cutscene = cutscene;
        if (transitionTime <= 0) {
            transitionTime = 1;
        }
        this.transitionTime = transitionTime;
        this.lockedTime = lockedTime;
    }

    public PointHandler createNew(int transitionTime, int lockedTime) {
        return this.cutscene.createPoint(transitionTime, lockedTime);
    }

    public void build() {
        QUEUE.putIfAbsent(this.cutscene.player, new ArrayList<>());
        List<Cutscene> updatedCutscene = new ArrayList<>();
        if (!this.cutscene.forced) {
            updatedCutscene.addAll(QUEUE.get(this.cutscene.player));
        } else {
            QUEUE.get(this.cutscene.player).forEach(Cutscene::stop);
        }
        updatedCutscene.add(this.cutscene);
        QUEUE.put(this.cutscene.player, updatedCutscene);
    }

    public int getTransitionTime() {
        return this.transitionTime;
    }

    public int getLockedTime() {
        return this.lockedTime;
    }

    public void tickPoints() {
        if (this.transitionTime > 0) {

            for (CutscenePoint point : this.points) {
                point.tickPoint();
            }

            this.transitionTime--;
        } else if (this.lockedTime > 0) {
            this.lockedTime--;
        }
    }

    protected void addPoint(CutscenePoint point) {
        this.points.add(point);
    }

    public PointHandler addVecPoint(Vec3 vecPoint, EasingFunctions easingFunction) {
        return addVecPoint(vecPoint, vecPoint, easingFunction);
    }

    public PointHandler addVecPoint(Vec3 from, Vec3 to, EasingFunctions easingFunction) {
        VecPoint point = new VecPoint(this.cutscene, this, from, to, this.transitionTime, this.lockedTime, easingFunction);
        addPoint(point);
        if (this.cutscene.spawnPoint == Vec3.ZERO)
            this.cutscene.spawnPoint = from;

        this.cutscene.target = this.cutscene.player.getEyePosition();

        return this;
    }

    public PointHandler addFacingRelativeVecPoint(Vec3 vec, EasingFunctions easingFunction) {
        return addFacingRelativeVecPoint(this.cutscene.player, vec, vec, easingFunction);
    }

    public PointHandler addFacingRelativeVecPoint(Vec3 from, Vec3 to, EasingFunctions easingFunction) {
        return addFacingRelativeVecPoint(this.cutscene.player, from, to, easingFunction);
    }

    public PointHandler addFacingRelativeVecPoint(LivingEntity relativeTo, Vec3 vec, EasingFunctions easingFunction) {
        return addFacingRelativeVecPoint(relativeTo, vec, vec, easingFunction);
    }

    public PointHandler addFacingRelativeVecPoint(LivingEntity relativeTo, Vec3 from, Vec3 to, EasingFunctions easingFunction) {
        Vec3[] positions = new Vec3[]{from, to};
        Vec3[] relativePositions = new Vec3[positions.length];
        for (int i = 0; i < positions.length; i++) {
            Vec3 pos = positions[i];
            Vec3 vec3 = relativeTo.getEyePosition();
            Vec3 vec31F = calculateViewVector(0, relativeTo.getYRot()).scale(pos.x);
            Vec3 vec31S = calculateViewVector(0, relativeTo.getYRot() + 90).scale(pos.z);
            Vec3 FBVector = vec3.add(vec31F);
            Vec3 RLVector = vec3.add(vec31S);
            Vec3 vectorDifference = FBVector.subtract(RLVector);
            Vec3 vec = vec3.add(vectorDifference);
            relativePositions[i] = vec.subtract(0.0F, 1.75F, 0.0F).add(0.0F, pos.y, 0.0F);
        }
        for (int i = 1; i < relativePositions.length; i++) {
            relativePositions[i] = relativePositions[i].subtract(0.5F, 0.0F, 0.5F);
        }
        return addVecPoint(relativePositions[0], relativePositions[1], easingFunction);
    }

}
