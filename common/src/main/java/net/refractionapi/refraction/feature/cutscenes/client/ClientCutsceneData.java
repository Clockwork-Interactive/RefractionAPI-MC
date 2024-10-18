package net.refractionapi.refraction.feature.cutscenes.client;

import net.minecraft.client.Minecraft;
import net.refractionapi.refraction.client.ClientData;
import net.refractionapi.refraction.helper.math.EasingFunctions;

public class ClientCutsceneData {

    public static int cameraID = -1;
    public static boolean hasBars = false;

    public static int startBarHeight = -1;
    public static int currentBarHeight = -1;
    public static int endBarHeight = -1;
    public static int transitionTicksBarHeight = -1;
    public static int progressTrackerBarHeight = 0;
    public static EasingFunctions easingFunctionBarHeight = EasingFunctions.LINEAR;

    public static float startRot = -1;
    public static float currentRot = -1;
    public static float endRot = -1;
    public static int transitionTicksRot = -1;
    public static int progressTrackerRot = 0;
    public static EasingFunctions easingFunctionRot = EasingFunctions.LINEAR;

    public static void startCutscene(int id, boolean start) {
        cameraID = id;
        ClientData.canRotateCamera = start;
        if (start) {
            assert Minecraft.getInstance().level != null;
            if (Minecraft.getInstance().level.getEntity(cameraID) != null) {
                Minecraft.getInstance().cameraEntity = Minecraft.getInstance().level.getEntity(cameraID);
                Minecraft.getInstance().gameRenderer.setRenderHand(false);
            }
        } else {
            Minecraft.getInstance().cameraEntity = Minecraft.getInstance().player;
            Minecraft.getInstance().gameRenderer.setRenderHand(true);
            hasBars = false;
        }
    }

    public static void setBarProps(boolean hasBars, int barHeight, int endBarHeight, float startRot, float endRot, int transitionTime, EasingFunctions easingFunction) {
        ClientCutsceneData.hasBars = hasBars;
        ClientCutsceneData.startBarHeight = barHeight;
        ClientCutsceneData.endBarHeight = endBarHeight;
        ClientCutsceneData.transitionTicksBarHeight = transitionTime;
        ClientCutsceneData.progressTrackerBarHeight = 0;
        ClientCutsceneData.easingFunctionBarHeight = easingFunction;
        ClientCutsceneData.startRot = startRot;
        ClientCutsceneData.endRot = endRot;
        ClientCutsceneData.transitionTicksRot = transitionTime;
        ClientCutsceneData.progressTrackerRot = 0;
        ClientCutsceneData.easingFunctionRot = easingFunction;
    }

    public static void reset() {
        startCutscene(-1, false);
        cameraID = -1;
        hasBars = false;
        startBarHeight = -1;
        currentBarHeight = -1;
        endBarHeight = -1;
        transitionTicksBarHeight = -1;
        progressTrackerBarHeight = 0;
        easingFunctionBarHeight = EasingFunctions.LINEAR;
        startRot = -1;
        currentRot = -1;
        endRot = -1;
        transitionTicksRot = -1;
        progressTrackerRot = 0;
        easingFunctionRot = EasingFunctions.LINEAR;
    }

}
