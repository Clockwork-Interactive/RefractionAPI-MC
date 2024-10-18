package net.refractionapi.refraction.feature.cutscenes.point;

import net.minecraft.world.phys.Vec3;
import net.refractionapi.refraction.feature.cutscenes.Cutscene;
import net.refractionapi.refraction.helper.math.EasingFunctions;

public class VecPoint extends CutscenePoint {

    private final Vec3 from;
    private final Vec3 to;
    public float delta;
    private int progressTracker;

    protected VecPoint(Cutscene cutscene, PointHandler pointHandler, Vec3 from, Vec3 to, int transitionTime, int lockedTime, EasingFunctions easingFunction) {
        super(cutscene, pointHandler, transitionTime, lockedTime, easingFunction);
        this.from = from;
        this.to = to;
    }

    @Override
    public void tickPoint() {
        this.progressTracker++;
        float delta = (float) this.progressTracker / (float) this.transitionTime;
        if (delta > 1.0F) return;
        delta = this.easingFunction.getEasing(delta);
        this.delta = delta;
        Vec3 lerpedVec = this.from.lerp(this.to, delta);
        this.cutscene.camera.setPos(lerpedVec.x, lerpedVec.y, lerpedVec.z);
    }

    @Override
    public void onSwitch() {
        this.cutscene.spawnPoint = this.from;
    }
}
