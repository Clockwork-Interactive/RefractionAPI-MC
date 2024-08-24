package net.refractionapi.refraction.cutscenes.point;

import net.minecraft.server.level.ServerPlayer;
import net.refractionapi.refraction.cutscenes.Cutscene;
import net.refractionapi.refraction.math.EasingFunctions;
import net.refractionapi.refraction.networking.RefractionMessages;
import net.refractionapi.refraction.networking.S2C.SetBarPropsS2CPacket;

public class BarPoint extends CutscenePoint {

    private final boolean hasBars;
    private final int barHeight;
    private final int endBarHeight;
    private final float startRot;
    private final float endRot;

    public BarPoint(Cutscene cutscene, PointHandler pointHandler, boolean hasBars, int barHeight, int endBarHeight, float startRot, float endRot, int transitionTime, int lockedTime, int startTime, EasingFunctions easingFunction) {
        super(cutscene, pointHandler, transitionTime, lockedTime, startTime, easingFunction);
        this.hasBars = hasBars;
        this.barHeight = barHeight;
        this.endBarHeight = endBarHeight;
        this.startRot = startRot;
        this.endRot = endRot;
    }

    @Override
    public void tickPoint() {

    }

    @Override
    public void onSwitch() {
        if (this.cutscene.livingEntity instanceof ServerPlayer serverPlayer)
            RefractionMessages.sendToPlayer(new SetBarPropsS2CPacket(this.hasBars, this.barHeight, this.endBarHeight, this.startRot, this.endRot, this.transitionTime, this.easingFunction), serverPlayer);
    }
}
