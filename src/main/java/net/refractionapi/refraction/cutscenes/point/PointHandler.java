package net.refractionapi.refraction.cutscenes.point;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;
import net.refractionapi.refraction.cutscenes.Cutscene;
import net.refractionapi.refraction.math.EasingFunctions;
import net.refractionapi.refraction.networking.RefractionMessages;
import net.refractionapi.refraction.networking.S2C.InvokeCutsceneS2CPacket;
import net.refractionapi.refraction.networking.S2C.SetBarPropsS2CPacket;
import net.refractionapi.refraction.networking.S2C.SetFOVS2CPacket;
import net.refractionapi.refraction.networking.S2C.SetZRotS2CPacket;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import static net.refractionapi.refraction.cutscenes.CutsceneHandler.QUEUE;
import static net.refractionapi.refraction.vec3.Vec3Helper.calculateViewVector;

public class PointHandler {

    public final Cutscene cutscene;
    protected final List<CutscenePoint> points = new ArrayList<>();
    protected int transitionTime;
    protected int lockedTime;
    protected boolean switched = false;
    protected Consumer<Cutscene> onSwitch;
    private boolean lockedPosition = true;
    private boolean lockedCamera = true;
    private boolean hideName = true;
    private boolean invulnerable = true;
    private boolean lockedLook = true;
    private Consumer<Cutscene> cameraTick;
    private Vec3 target;


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
        QUEUE.putIfAbsent(this.cutscene.livingEntity, new ArrayList<>());
        List<Cutscene> updatedCutscene = new ArrayList<>();
        if (!this.cutscene.forced) {
            updatedCutscene.addAll(QUEUE.get(this.cutscene.livingEntity));
        } else {
            QUEUE.get(this.cutscene.livingEntity).forEach(Cutscene::stop);
        }
        updatedCutscene.add(this.cutscene);
        QUEUE.put(this.cutscene.livingEntity, updatedCutscene);
        return this.cutscene;
    }

    public void onSwitch() {
        if (this.cutscene.livingEntity instanceof ServerPlayer serverPlayer) {
            RefractionMessages.sendToPlayer(new SetFOVS2CPacket(-1), serverPlayer);
            RefractionMessages.sendToPlayer(new SetZRotS2CPacket(-1), serverPlayer);
        }
        this.points.forEach(CutscenePoint::onSwitch);
        this.switched = true;
        this.cutscene.camera.discard();
        if (this.target != null)
            this.cutscene.target = this.target;
        this.cutscene.createCamera();
        this.cutscene.lockPosition(this.lockedPosition);
        this.cutscene.lockCamera(this.lockedCamera);
        this.cutscene.setInvulnerable(this.invulnerable);
        this.cutscene.hideName(this.hideName);
        this.cutscene.lockLook(this.lockedLook);
        this.cutscene.tickCamera(this.cameraTick);
        if (this.cutscene.livingEntity instanceof ServerPlayer serverPlayer)
            RefractionMessages.sendToPlayer(new InvokeCutsceneS2CPacket(this.cutscene.camera.getId(), true), serverPlayer);
        if (this.onSwitch != null)
            this.onSwitch.accept(this.cutscene);
    }

    public PointHandler onSwitch(Consumer<Cutscene> onSwitch) {
        this.onSwitch = onSwitch;
        return this;
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

    public PointHandler lockPosition(boolean lock) {
        this.lockedPosition = lock;
        return this;
    }

    public PointHandler lockCamera(boolean lock) {
        this.lockedCamera = lock;
        return this;
    }

    public PointHandler hideName(boolean hide) {
        this.hideName = hide;
        return this;
    }

    public PointHandler setInvulnerable(boolean invulnerable) {
        this.invulnerable = invulnerable;
        return this;
    }

    public PointHandler lockLook(boolean lock) {
        this.lockedLook = lock;
        return this;
    }

    public PointHandler tickCamera(Consumer<Cutscene> cameraTick) {
        this.cameraTick = cameraTick;
        return this;
    }

    protected void addPoint(CutscenePoint point) {
        this.points.add(point);
    }

    public PointHandler addVecPoint(Vec3 vecPoint) {
        return addVecPoint(vecPoint, vecPoint, EasingFunctions.LINEAR);
    }

    public PointHandler addVecPoint(Vec3 vecPoint, EasingFunctions easingFunction) {
        return addVecPoint(vecPoint, vecPoint, easingFunction);
    }

    public PointHandler addVecPoint(Vec3 from, Vec3 to, EasingFunctions easingFunction) {
        return addVecPoint(from, to, 0, easingFunction);
    }

    public PointHandler addVecPoint(Vec3 from, Vec3 to, int startTime, EasingFunctions easingFunction) {
        return addVecPoint(from, to, startTime, this.transitionTime, easingFunction);
    }

    public PointHandler addVecPoint(Vec3 from, Vec3 to, int startTime, int transitionTime, EasingFunctions
            easingFunction) {
        VecPoint point = new VecPoint(this.cutscene, this, from, to, transitionTime - startTime, this.lockedTime, easingFunction);
        point.startTime = startTime;
        addPoint(point);
        return this;
    }

    public PointHandler addFacingRelativeVecPoint(Vec3 vec) {
        return addFacingRelativeVecPoint(this.cutscene.livingEntity, 0, vec, vec, EasingFunctions.LINEAR);
    }

    public PointHandler addFacingRelativeVecPoint(LivingEntity relativeTo, Vec3 vec) {
        return addFacingRelativeVecPoint(relativeTo, 0, vec, vec, EasingFunctions.LINEAR);
    }

    public PointHandler addFacingRelativeVecPoint(LivingEntity relativeTo, Vec3 start, Vec3 end, EasingFunctions
            easingFunction) {
        return addFacingRelativeVecPoint(relativeTo, 0, start, end, easingFunction);
    }

    public PointHandler addFacingRelativeVecPoint(LivingEntity relativeTo, Vec3 start, Vec3 end,
                                                  int startTime, EasingFunctions easingFunction) {
        return addFacingRelativeVecPoint(relativeTo, startTime, start, end, easingFunction);
    }

    public PointHandler addFacingRelativeVecPoint(Vec3 from, Vec3 to, EasingFunctions easingFunction) {
        return addFacingRelativeVecPoint(this.cutscene.livingEntity, 0, from, to, easingFunction);
    }

    public PointHandler addFacingRelativeVecPoint(Vec3 from, Vec3 to, int startTime, EasingFunctions easingFunction) {
        return addFacingRelativeVecPoint(this.cutscene.livingEntity, startTime, from, to, easingFunction);
    }

    public PointHandler addFacingRelativeVecPoint(LivingEntity relativeTo, int startTime, Vec3 from, Vec3
            to, EasingFunctions easingFunction) {
        return addFacingRelativeVecPoint(relativeTo, startTime, this.transitionTime, from, to, easingFunction);
    }

    public PointHandler addFacingRelativeVecPoint(LivingEntity relativeTo, int startTime, int transitionTime, Vec3
            from, Vec3 to, EasingFunctions easingFunction) {
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
        return addVecPoint(relativePositions[0], relativePositions[1], startTime, transitionTime, easingFunction);
    }

    public PointHandler setTarget(Vec3 target) {
        TargetPoint point = new TargetPoint(this.cutscene, this, target, this.transitionTime, this.lockedTime, EasingFunctions.LINEAR);
        this.target = target;
        addPoint(point);
        return this;
    }

    public PointHandler setTarget(LivingEntity target) {
        return setTarget(target, Vec3.ZERO);
    }

    public PointHandler setTarget(LivingEntity target, Vec3 offset) {
        TargetPoint point = new TargetPoint(this.cutscene, this, target, offset, this.transitionTime, this.lockedTime, EasingFunctions.LINEAR);
        this.target = target.getEyePosition();
        addPoint(point);
        return this;
    }

    public PointHandler setTarget(Vec3 start, Vec3 end, EasingFunctions easingFunction) {
        return setTarget(start, end, this.transitionTime, easingFunction);
    }

    public PointHandler setTarget(Vec3 start, Vec3 end, int transitionTime, EasingFunctions easingFunction) {
        return setTarget(start, end, transitionTime, 0, easingFunction);
    }

    public PointHandler setTarget(Vec3 start, Vec3 end, int transitionTime, int startTime, EasingFunctions
            easingFunction) {
        TargetPoint point = new TargetPoint(this.cutscene, this, start, end, transitionTime, this.lockedTime, easingFunction);
        this.target = start;
        point.startTime = startTime;
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

    public PointHandler setFOV(int startFOV, int endFOV, int transitionTime, int startTime, EasingFunctions
            easingFunction) {
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

    public PointHandler setZRot(float startZRot, float endZRot, int transitionTime, int startTime, EasingFunctions
            easingFunction) {
        ZRotPoint point = new ZRotPoint(this.cutscene, this, startZRot, endZRot, transitionTime, this.lockedTime, startTime, easingFunction);
        addPoint(point);
        return this;
    }

    public PointHandler setBarProps(boolean hasBars, int barHeight, int endBarHeight) {
        return setBarProps(hasBars, barHeight, endBarHeight, 0, 0, 0, this.transitionTime, EasingFunctions.LINEAR);
    }

    public PointHandler setBarProps(int barHeight, int endBarHeight) {
        return setBarProps(true, barHeight, endBarHeight, 0, 0, 0, this.transitionTime, EasingFunctions.LINEAR);
    }

    public PointHandler setBarProps(int barHeight, int endBarHeight, float startRot, float endRot) {
        return setBarProps(true, barHeight, endBarHeight, startRot, endRot, 0, this.transitionTime, EasingFunctions.LINEAR);
    }

    public PointHandler setBarProps(boolean hasBars, int barHeight, int endBarHeight, float startRot, float endRot, int startTime, EasingFunctions easingFunction) {
        BarPoint point = new BarPoint(this.cutscene, this, hasBars, barHeight, endBarHeight, startRot, endRot, transitionTime, this.lockedTime, startTime, easingFunction);
        addPoint(point);
        return this;
    }

    public PointHandler setBarProps(boolean hasBars, int barHeight, int endBarHeight, float startRot, float endRot, int startTime, int transitionTime, EasingFunctions easingFunction) {
        BarPoint point = new BarPoint(this.cutscene, this, hasBars, barHeight, endBarHeight, startRot, endRot, transitionTime, this.lockedTime, startTime, easingFunction);
        addPoint(point);
        return this;
    }


}
