package net.refractionapi.refraction.client;

import net.minecraft.client.Minecraft;
import net.refractionapi.refraction.cutscenes.client.ClientCutsceneData;
import net.refractionapi.refraction.math.EasingFunctions;
import net.refractionapi.refraction.mixininterfaces.ICameraMixin;

public class ClientData {

    public static boolean canMove = true;
    public static boolean canRotateCamera = true;

    public static int startFOV = -1;
    public static double currentFOV = -1;
    public static int endFOV = -1;
    public static int transitionTicksFOV = -1;
    public static int progressTrackerFOV = 0;
    public static EasingFunctions easingFunctionFOV = EasingFunctions.LINEAR;

    public static float startZRot = -1;
    public static float currentZRot = -1;
    public static float endZRot = -1;
    public static int transitionTicksZRot = -1;
    public static int progressTrackerZRot = 0;
    public static EasingFunctions easingFunctionZRot = EasingFunctions.LINEAR;


    public static void reset() {
        canMove = true;
        canRotateCamera = true;
        startFOV = -1;
        endFOV = -1;
        transitionTicksFOV = -1;
        progressTrackerFOV = 0;
        easingFunctionFOV = EasingFunctions.LINEAR;
        startZRot = -1;
        endZRot = -1;
        transitionTicksZRot = -1;
        progressTrackerZRot = 0;
        easingFunctionZRot = EasingFunctions.LINEAR;
        ClientCutsceneData.cameraID = -1;
        Minecraft.getInstance().gameRenderer.setRenderHand(true);
    }

}
