package net.refractionapi.refraction.feature.cutscenes.point;

import net.refractionapi.refraction.feature.cutscenes.Cutscene;
import net.refractionapi.refraction.helper.math.EasingFunctions;

public class RotationPoint extends CutscenePoint {

    protected RotationPoint(Cutscene cutscene, PointHandler pointHandler, float startZ, float endZ, int transitionTime, int lockedTime, int startTime, EasingFunctions easingFunction) {
        super(cutscene, pointHandler, transitionTime, lockedTime, startTime, easingFunction);
    }

    @Override
    public void tickPoint() {

    }

}
