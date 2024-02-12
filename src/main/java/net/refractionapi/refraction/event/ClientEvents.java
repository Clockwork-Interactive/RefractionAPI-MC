package net.refractionapi.refraction.event;

import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ClientPlayerNetworkEvent;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.client.event.RenderGuiOverlayEvent;
import net.minecraftforge.client.event.ViewportEvent;
import net.minecraftforge.client.gui.overlay.IGuiOverlay;
import net.minecraftforge.client.gui.overlay.NamedGuiOverlay;
import net.minecraftforge.client.gui.overlay.VanillaGuiOverlay;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.refractionapi.refraction.Refraction;
import net.refractionapi.refraction.client.ClientData;
import net.refractionapi.refraction.cutscenes.client.ClientCutsceneData;

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
    public static void fovEvent(ViewportEvent.ComputeFov event) {
        if (ClientData.FOV != -1)
            event.setFOV(ClientData.FOV);
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
