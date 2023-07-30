package net.refractionapi.refraction.cutscenes.client;

import net.minecraft.client.Minecraft;

public class ClientCutsceneData {

    public static int cameraID;
    public static boolean inCutscene = false;

    public static void startCutscene(int id, boolean start) {
        cameraID = id;
        inCutscene = start;
        if (start) {
            assert Minecraft.getInstance().level != null;
            if (Minecraft.getInstance().level.getEntity(cameraID) != null) {
                Minecraft.getInstance().cameraEntity = Minecraft.getInstance().level.getEntity(cameraID);
            }
        } else {
            Minecraft.getInstance().cameraEntity = Minecraft.getInstance().player;;
        }
    }

}
