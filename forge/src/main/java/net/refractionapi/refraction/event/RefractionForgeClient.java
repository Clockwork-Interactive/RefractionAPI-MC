package net.refractionapi.refraction.event;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.refractionapi.refraction.Refraction;
import net.refractionapi.refraction.events.event.RefractionClientEvents;

@Mod.EventBusSubscriber(modid = Refraction.MOD_ID, value = Dist.CLIENT)
public class RefractionForgeClient {

    @SubscribeEvent
    public static void clientTick(TickEvent.ClientTickEvent event) {
        RefractionClientEvents.clientTick(event.phase.equals(TickEvent.Phase.END));
    }

}
