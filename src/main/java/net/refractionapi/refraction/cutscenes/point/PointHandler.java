package net.refractionapi.refraction.cutscenes.point;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;
import net.refractionapi.refraction.cutscenes.Cutscene;
import net.refractionapi.refraction.math.EasingFunctions;
import net.refractionapi.refraction.networking.RefractionMessages;
import net.refractionapi.refraction.networking.S2C.InvokeCutsceneS2CPacket;
import net.refractionapi.refraction.networking.S2C.SetFOVS2CPacket;
import net.refractionapi.refraction.networking.S2C.SetZRotS2CPacket;

import java.util.ArrayList;
import java.util.List;

import static net.refractionapi.refraction.cutscenes.CutsceneHandler.QUEUE;
import static net.refractionapi.refraction.vec3.Vec3Helper.calculateViewVector;

public class PointHandler {

    public final Cutscene cutscene;
    protected final List<CutscenePoint> points = new ArrayList<>();
    protected int transitionTime;
    protected int lockedTime;
    protected boolean switched = false;

    public PointHandler(Cutscene cutscene, int transitionTime, int lockedTime) {
        this.cutscene = cutscene;
        if (transitionTime <= 0) {
            transitionTime = 1;
        }
        this.transitionTime = transitionTime;
        this.lockedTime = lockedTime;
    }

    public PointHandler newPoint(int transitionTime, int lockedTime) {
        return this.cutscene.createPoint(transitionTime, lockedTime);
    }

    public Cutscene build() {
        QUEUE.putIfAbsent(this.cutscene.player, new ArrayList<>());
        List<Cutscene> updatedCutscene = new ArrayList<>();
        if (!this.cutscene.forced) {
            updatedCutscene.addAll(QUEUE.get(this.cutscene.player));
        } else {
            QUEUE.get(this.cutscene.player).forEach(Cutscene::stop);
        }
        updatedCutscene.add(this.cutscene);
        QUEUE.put(this.cutscene.player, updatedCutscene);
        return this.cutscene;
    }

    public void onSwitch() {
        RefractionMessages.sendToPlayer(new SetFOVS2CPacket(-1), this.cutscene.player);
        RefractionMessages.sendToPlayer(new SetZRotS2CPacket(-1), this.cutscene.player);
        this.points.forEach(CutscenePoint::onSwitch);
        this.switched = true;
        this.cutscene.camera.discard();
        this.cutscene.createCamera();
        RefractionMessages.sendToPlayer(new InvokeCutsceneS2CPacket(this.cutscene.camera.getId(), true), this.cutscene.player);
    }

    public boolean isSwitched() {
        return this.switched;
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
                if (point.startTime > 0) {
                    point.startTime--;
                    continue;
                }
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
        return addVecPoint(from, to, 0, easingFunction);
    }

    public PointHandler addVecPoint(Vec3 from, Vec3 to, int startTime, EasingFunctions easingFunction) {
        VecPoint point = new VecPoint(this.cutscene, this, from, to, this.transitionTime - startTime, this.lockedTime, easingFunction);
        point.startTime = startTime;
        addPoint(point);
        return this;
    }

    public PointHandler addFacingRelativeVecPoint(Vec3 vec, EasingFunctions easingFunction) {
        return addFacingRelativeVecPoint(this.cutscene.player, 0, vec, vec, easingFunction);
    }

    public PointHandler addFacingRelativeVecPoint(LivingEntity relativeTo, Vec3 vec, EasingFunctions easingFunction) {
        return addFacingRelativeVecPoint(relativeTo, 0, vec, vec, easingFunction);
    }

    public PointHandler addFacingRelativeVecPoint(Vec3 from, Vec3 to, EasingFunctions easingFunction) {
        return addFacingRelativeVecPoint(this.cutscene.player, 0, from, to, easingFunction);
    }

    public PointHandler addFacingRelativeVecPoint(Vec3 from, Vec3 to, int startTime, EasingFunctions easingFunction) {
        return addFacingRelativeVecPoint(this.cutscene.player, startTime, from, to, easingFunction);
    }

    public PointHandler addFacingRelativeVecPoint(LivingEntity relativeTo, int startTime, Vec3 from, Vec3 to, EasingFunctions easingFunction) {
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
        return addVecPoint(relativePositions[0], relativePositions[1], startTime, easingFunction);
    }

    public PointHandler setTarget(Vec3 target) {
        TargetPoint point = new TargetPoint(this.cutscene, this, target, this.transitionTime, this.lockedTime, EasingFunctions.LINEAR);
        addPoint(point);
        return this;
    }

    public PointHandler setTarget(LivingEntity target) {
        TargetPoint point = new TargetPoint(this.cutscene, this, target, this.transitionTime, this.lockedTime, EasingFunctions.LINEAR);
        addPoint(point);
        return this;
    }

    public PointHandler setTarget(Vec3 start, Vec3 end, EasingFunctions easingFunction) {
        TargetPoint point = new TargetPoint(this.cutscene, this, start, end, this.transitionTime, this.lockedTime, easingFunction);
        addPoint(point);
        return this;
    }

    public PointHandler setFOV(int fov) {
        return setFOV(fov, fov);
    }

    public PointHandler setFOV(int startFOV, int endFOV) {
        return setFOV(startFOV, endFOV, EasingFunctions.LINEAR);
    }

    public PointHandler setFOV(int startFOV, int endFOV, EasingFunctions easingFunction) {
        return setFOV(startFOV, endFOV, this.transitionTime, easingFunction);
    }

    public PointHandler setFOV(int startFOV, int endFOV, int transitionTime, EasingFunctions easingFunction) {
        return setFOV(startFOV, endFOV, transitionTime, 0, easingFunction);
    }

    public PointHandler setFOV(int startFOV, int endFOV, int transitionTime, int startTime, EasingFunctions easingFunction) {
        FOVPoint point = new FOVPoint(this.cutscene, this, startFOV, endFOV, transitionTime, this.lockedTime, startTime, easingFunction);
        addPoint(point);
        return this;
    }

    public PointHandler setZRot(float zRot) {
        return setZRot(zRot, zRot);
    }

    public PointHandler setZRot(float startZRot, float endZRot) {
        return setZRot(startZRot, endZRot, EasingFunctions.LINEAR);
    }

    public PointHandler setZRot(float startZRot, float endZRot, EasingFunctions easingFunction) {
        return setZRot(startZRot, endZRot, this.transitionTime, easingFunction);
    }

    public PointHandler setZRot(float startZRot, float endZRot, int transitionTime, EasingFunctions easingFunction) {
        return setZRot(startZRot, endZRot, transitionTime, 0, easingFunction);
    }

    public PointHandler setZRot(float startZRot, float endZRot, int transitionTime, int startTime, EasingFunctions easingFunction) {
        ZRotPoint point = new ZRotPoint(this.cutscene, this, startZRot, endZRot, transitionTime, this.lockedTime, startTime, easingFunction);
        addPoint(point);
        return this;
    }


}
