package net.refractionapi.refraction.event;

import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ClientPlayerNetworkEvent;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.client.event.RenderGuiOverlayEvent;
import net.minecraftforge.client.event.ViewportEvent;
import net.minecraftforge.client.gui.overlay.VanillaGuiOverlay;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.refractionapi.refraction.Refraction;
import net.refractionapi.refraction.client.ClientData;
import net.refractionapi.refraction.cutscenes.client.ClientCutsceneData;
import net.refractionapi.refraction.mixininterfaces.ICameraMixin;

import java.util.ArrayList;
import java.util.List;

@Mod.EventBusSubscriber(modid = Refraction.MOD_ID, value = Dist.CLIENT)
public class ClientEvents {

    private static final List<ResourceLocation> overlays = new ArrayList<>();

    @SubscribeEvent
    public static void loggedOut(ClientPlayerNetworkEvent.LoggingOut event) {
        ClientData.reset();
    }

    @SubscribeEvent
    public static void clientTick(TickEvent.ClientTickEvent event) {
        if (event.phase.equals(TickEvent.Phase.END)) return;
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

    }

    @SubscribeEvent
    public static void fovEvent(ViewportEvent.ComputeFov event) {
        if (ClientData.currentFOV != -1)
            event.setFOV(ClientData.currentFOV);
    }

    @SubscribeEvent
    public static void cameraSetup(ViewportEvent.ComputeCameraAngles event) {
        if (ClientData.currentZRot != -1)
            event.setRoll(ClientData.currentZRot);
    }

    @SubscribeEvent
    public static void onMouse(InputEvent.InteractionKeyMappingTriggered event) {
        if (event.isAttack()) { // Crash fix when in cutscenes
            if (ClientCutsceneData.cameraID != -1) {
                event.setCanceled(true);
            }
        }
    }

    @SubscribeEvent
    public static void renderOverlays(RenderGuiOverlayEvent.Pre event) {
        if (ClientCutsceneData.cameraID != -1) {
            if (event.getOverlay() == VanillaGuiOverlay.CHAT_PANEL.type() || event.getOverlay() == VanillaGuiOverlay.VIGNETTE.type() || event.getOverlay() == VanillaGuiOverlay.RECORD_OVERLAY.type() || overlays.contains(event.getOverlay().id())) {
                return;
            }
            event.setCanceled(true);
        }
    }

    public static void addOverlayExclusion(ResourceLocation overlay) {
        overlays.add(overlay);
    }

}
