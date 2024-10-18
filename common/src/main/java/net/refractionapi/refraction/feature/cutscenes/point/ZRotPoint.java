package net.refractionapi.refraction.feature.cutscenes.point;

import net.minecraft.server.level.ServerPlayer;
import net.refractionapi.refraction.feature.cutscenes.Cutscene;
import net.refractionapi.refraction.helper.math.EasingFunctions;
import net.refractionapi.refraction.networking.RefractionMessages;
import net.refractionapi.refraction.networking.S2C.SetZRotS2CPacket;

public class ZRotPoint extends CutscenePoint {

    private final float start;
    private final float end;

    protected ZRotPoint(Cutscene cutscene, PointHandler pointHandler, float start, float end, int transitionTime, int lockedTime, int startTime, EasingFunctions easingFunction) {
        super(cutscene, pointHandler, transitionTime, lockedTime, startTime, easingFunction);
        this.start = start;
        this.end = end;
    }

    @Override
    public void tickPoint() {

    }

    @Override
    public void onSwitch() {
        if (this.cutscene.livingEntity instanceof ServerPlayer serverPlayer)
            RefractionMessages.sendToPlayer(new SetZRotS2CPacket(this.start, this.end, this.transitionTime, this.easingFunction), serverPlayer);
    }
}
