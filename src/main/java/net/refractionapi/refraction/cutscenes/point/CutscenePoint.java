package net.refractionapi.refraction.cutscenes.point;

import net.refractionapi.refraction.cutscenes.Cutscene;
import net.refractionapi.refraction.math.EasingFunction;
import net.refractionapi.refraction.math.EasingFunctions;

public abstract class CutscenePoint {

    public final Cutscene cutscene;
    public final PointHandler pointHandler;
    protected int transitionTime;
    protected int lockedTime;
    protected int startTime = 0;
    protected EasingFunctions easingFunction;

    protected CutscenePoint(Cutscene cutscene, PointHandler pointHandler, int transitionTime, int lockedTime, EasingFunctions easingFunction) {
        this.cutscene = cutscene;
        this.pointHandler = pointHandler;
        this.transitionTime = transitionTime;
        this.lockedTime = lockedTime;
        this.easingFunction = easingFunction;
    }

    protected CutscenePoint(Cutscene cutscene, PointHandler pointHandler, int transitionTime, int lockedTime, int startTime,EasingFunctions easingFunction) {
        this.cutscene = cutscene;
        this.pointHandler = pointHandler;
        this.transitionTime = transitionTime;
        this.lockedTime = lockedTime;
        this.startTime = startTime;
        this.easingFunction = easingFunction;
    }

    public abstract void tickPoint();

    public void onSwitch() {

    }

}
