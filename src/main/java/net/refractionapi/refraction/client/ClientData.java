package net.refractionapi.refraction.client;

import net.minecraft.client.Minecraft;
import net.refractionapi.refraction.cutscenes.client.ClientCutsceneData;

public class ClientData {

    public static boolean canMove = true;
    public static boolean canRotateCamera = true;
    public static int FOV = -1;

    public static void reset() {
        canMove = true;
        canRotateCamera = true;
        FOV = -1;
        ClientCutsceneData.cameraID = -1;
        Minecraft.getInstance().gameRenderer.setRenderHand(true);
    }

}
