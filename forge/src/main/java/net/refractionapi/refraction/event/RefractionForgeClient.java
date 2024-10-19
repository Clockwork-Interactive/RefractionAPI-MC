package net.refractionapi.refraction.event;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterGuiOverlaysEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.refractionapi.refraction.Refraction;
import net.refractionapi.refraction.events.event.RefractionClientEvents;

@Mod.EventBusSubscriber(modid = Refraction.MOD_ID, value = Dist.CLIENT)
public class RefractionForgeClient {

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void registerOverlays(RegisterGuiOverlaysEvent event) {
        event.registerAboveAll("quest", (gui, guiGraphics, partialTicks, x, y) -> QuestRenderer.quest(guiGraphics, partialTicks));
        event.registerAboveAll("cinematic", (gui, guiGraphics, partialTicks, x, y) -> CinematicBars.bars(guiGraphics, partialTicks));
    @SubscribeEvent
    public static void clientTick(TickEvent.ClientTickEvent event) {
        RefractionClientEvents.clientTick(event.phase.equals(TickEvent.Phase.END));
    }

}
