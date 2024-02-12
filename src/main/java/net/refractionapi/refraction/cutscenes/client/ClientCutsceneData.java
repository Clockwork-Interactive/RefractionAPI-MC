package net.refractionapi.refraction.cutscenes.client;

import net.minecraft.client.Minecraft;
import net.refractionapi.refraction.client.ClientData;

public class ClientCutsceneData {

    public static int cameraID = -1;

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
        }
    }

}
