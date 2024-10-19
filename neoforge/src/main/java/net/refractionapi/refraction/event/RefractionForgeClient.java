package net.refractionapi.refraction.event;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.refractionapi.refraction.Refraction;
import net.refractionapi.refraction.events.event.RefractionClientEvents;

@EventBusSubscriber(modid = Refraction.MOD_ID, value = Dist.CLIENT, bus = EventBusSubscriber.Bus.GAME)
public class RefractionForgeClient {

    @SubscribeEvent
    public static void clientTick(ClientTickEvent.Pre event) {
        RefractionClientEvents.clientTick(false);
    }

    @SubscribeEvent
    public static void clientTick(ClientTickEvent.Post event) {
        RefractionClientEvents.clientTick(true);
    }

}
