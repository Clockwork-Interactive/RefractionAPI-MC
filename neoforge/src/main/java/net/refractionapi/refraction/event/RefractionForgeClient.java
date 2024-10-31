package net.refractionapi.refraction.event;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.InputEvent;
import net.neoforged.neoforge.client.event.ScreenEvent;
import net.refractionapi.refraction.Refraction;
import net.refractionapi.refraction.events.event.RefractionClientEvents;
import net.refractionapi.refraction.feature.screen.RefractionScreen;

@EventBusSubscriber(modid = Refraction.MOD_ID, value = Dist.CLIENT, bus = EventBusSubscriber.Bus.GAME)
public class RefractionForgeClient {

    @SubscribeEvent
    public static void closeScreen(ScreenEvent.Closing event) {
        if (event.getScreen() instanceof RefractionScreen) {
            RefractionClientEvents.onRemove(event.getScreen());
        }
    }

    @SubscribeEvent
    public static void clientTick(ClientTickEvent.Pre event) {
        RefractionClientEvents.clientTick(false);
    }

    @SubscribeEvent
    public static void clientTickPost(ClientTickEvent.Post event) {
        RefractionClientEvents.clientTick(true);
    }

    @SubscribeEvent
    public static void attackEvent(InputEvent.InteractionKeyMappingTriggered event) {
        if (event.isAttack()) {
            event.setCanceled(RefractionClientEvents.onAttack());
        }
    }

}
