package net.refractionapi.refraction.feature.cutscenes.point;

import net.minecraft.commands.arguments.EntityAnchorArgument;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;
import net.refractionapi.refraction.feature.cutscenes.Cutscene;
import net.refractionapi.refraction.helper.math.EasingFunctions;

public class TargetPoint extends CutscenePoint {

    protected Vec3 target;
    protected Vec3 start;
    protected Vec3 end;
    protected LivingEntity dynamicTarget;
    protected Vec3 offset;
    protected int progressTracker;

    protected TargetPoint(Cutscene cutscene, PointHandler pointHandler, Vec3 target, int transitionTime, int lockedTime, EasingFunctions easingFunction) {
        super(cutscene, pointHandler, transitionTime, lockedTime, easingFunction);
        this.target = target;
        this.cutscene.target = target;
    }

    protected TargetPoint(Cutscene cutscene, PointHandler pointHandler, LivingEntity dynamicTarget, Vec3 offset, int transitionTime, int lockedTime, EasingFunctions easingFunction) {
        super(cutscene, pointHandler, transitionTime, lockedTime, easingFunction);
        this.dynamicTarget = dynamicTarget;
        this.offset = offset;
        this.cutscene.target = dynamicTarget.getEyePosition();
    }

    protected TargetPoint(Cutscene cutscene, PointHandler pointHandler, Vec3 start, Vec3 end, int transitionTime, int lockedTime, EasingFunctions easingFunction) {
        super(cutscene, pointHandler, transitionTime, lockedTime, easingFunction);
        this.target = start;
        this.cutscene.target = start;
        this.start = start;
        this.end = end;
    }


    @Override
    public void tickPoint() {
        this.progressTracker++;
        if (this.dynamicTarget != null) {
            this.target = this.dynamicTarget.getEyePosition().add(this.offset);
        }
        if (this.start != null) {
            this.target = this.start.lerp(this.end, this.easingFunction.getEasing((float) this.progressTracker / (float) this.transitionTime));
        }
        this.cutscene.target = this.target;
        if (this.cutscene.lockedCamera)
            this.cutscene.camera.lookAt(EntityAnchorArgument.Anchor.EYES, this.target);
    }
}
