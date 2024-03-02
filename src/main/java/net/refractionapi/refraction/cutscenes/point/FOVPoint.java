package net.refractionapi.refraction.cutscenes.point;

import net.refractionapi.refraction.cutscenes.Cutscene;
import net.refractionapi.refraction.math.EasingFunctions;
import net.refractionapi.refraction.networking.RefractionMessages;
import net.refractionapi.refraction.networking.S2C.SetFOVS2CPacket;

public class FOVPoint extends CutscenePoint {

    private final int startFOV;
    private final int endFOV;

    protected FOVPoint(Cutscene cutscene, PointHandler pointHandler, int startFOV, int endFOV, int transitionTime, int lockedTime, int startTime, EasingFunctions easingFunction) {
        super(cutscene, pointHandler, transitionTime, lockedTime, startTime, easingFunction);
        this.startFOV = startFOV;
        this.endFOV = endFOV;
    }

    @Override
    public void tickPoint() {

    }

    @Override
    public void onSwitch() {
        RefractionMessages.sendToPlayer(new SetFOVS2CPacket(this.startFOV, this.endFOV, this.transitionTime, this.easingFunction), this.cutscene.player);
    }

}
