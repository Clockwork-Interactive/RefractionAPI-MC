package net.refractionapi.refraction.event;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.refractionapi.refraction.client.ClientData;
import net.refractionapi.refraction.cutscenes.client.ClientCutsceneData;
import net.refractionapi.refraction.screen.RefractionScreen;

import java.util.ArrayList;
import java.util.List;

public class RefractionClientEvents {

    public static final List<ResourceLocation> overlays = new ArrayList<>();

    public static void onRemove(Screen screen) {
        if (screen instanceof RefractionScreen) {
            ClientData.screenHandler.onClose(false);
        }
    }

    public static void loggedOut() {
        ClientData.reset();
    }

    public static void clientTick(boolean post) {
        if (post) return;
        if (Minecraft.getInstance().isSingleplayer() && Minecraft.getInstance().isPaused()) return;
        if (ClientData.startFOV != -1) {
            if (ClientData.progressTrackerFOV < ClientData.transitionTicksFOV) {
                ClientData.progressTrackerFOV++;
                float delta = (float) ClientData.progressTrackerFOV / (float) ClientData.transitionTicksFOV;
                float eased = ClientData.easingFunctionFOV.getEasing(delta);
                ClientData.currentFOV = Mth.lerp(eased, (float) ClientData.startFOV, (float) ClientData.endFOV);
            }
        }

        if (ClientData.startZRot != -1) {
            if (ClientData.progressTrackerZRot < ClientData.transitionTicksZRot) {
                ClientData.progressTrackerZRot++;
                float delta = (float) ClientData.progressTrackerZRot / (float) ClientData.transitionTicksZRot;
                float eased = ClientData.easingFunctionZRot.getEasing(delta);
                ClientData.currentZRot = Mth.lerp(eased, ClientData.startZRot, ClientData.endZRot);
            }
        }

        if (ClientCutsceneData.hasBars) {

            if (ClientCutsceneData.startBarHeight != -1) {
                if (ClientCutsceneData.progressTrackerBarHeight < ClientCutsceneData.transitionTicksBarHeight) {
                    ClientCutsceneData.progressTrackerBarHeight++;
                    float delta = (float) ClientCutsceneData.progressTrackerBarHeight / (float) ClientCutsceneData.transitionTicksBarHeight;
                    float eased = ClientCutsceneData.easingFunctionBarHeight.getEasing(delta);
                    ClientCutsceneData.currentBarHeight = (int) Mth.lerp(eased, ClientCutsceneData.startBarHeight, ClientCutsceneData.endBarHeight);
                }
            }

            if (ClientCutsceneData.startRot != -1) {
                if (ClientCutsceneData.progressTrackerRot < ClientCutsceneData.transitionTicksRot) {
                    ClientCutsceneData.progressTrackerRot++;
                    float delta = (float) ClientCutsceneData.progressTrackerRot / (float) ClientCutsceneData.transitionTicksRot;
                    float eased = ClientCutsceneData.easingFunctionRot.getEasing(delta);
                    ClientCutsceneData.currentRot = Mth.lerp(eased, ClientCutsceneData.startRot, ClientCutsceneData.endRot);
                }
            }

        }

    }

    public static boolean onAttack() {
        return ClientCutsceneData.cameraID != -1;
    }

}
