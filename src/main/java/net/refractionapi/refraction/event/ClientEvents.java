package net.refractionapi.refraction.event;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ClientPlayerNetworkEvent;
import net.minecraftforge.client.event.ViewportEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.refractionapi.refraction.Refraction;
import net.refractionapi.refraction.client.ClientData;

@Mod.EventBusSubscriber(modid = Refraction.MOD_ID, value = Dist.CLIENT)
public class ClientEvents {

    @SubscribeEvent
    public static void loggedOut(ClientPlayerNetworkEvent.LoggingOut event) {
        ClientData.reset();
    }

    @SubscribeEvent
    public static void fovEvent(ViewportEvent.ComputeFov event) {
        if (ClientData.FOV != -1)
            event.setFOV(ClientData.FOV);
    }

}
