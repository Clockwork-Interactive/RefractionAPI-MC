package net.refractionapi.refraction.event;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.client.event.RegisterGuiOverlaysEvent;
import net.minecraftforge.client.event.ScreenEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.refractionapi.refraction.Refraction;
import net.refractionapi.refraction.feature.cutscenes.client.CinematicBars;
import net.refractionapi.refraction.feature.quest.client.QuestRenderer;
import net.refractionapi.refraction.events.event.RefractionClientMEvents;
import net.refractionapi.refraction.feature.screen.RefractionScreen;

@Mod.EventBusSubscriber(modid = Refraction.MOD_ID, value = Dist.CLIENT)
public class RefractionForgeClient {
    @SubscribeEvent
    public static void closeScreen(ScreenEvent.Closing event) {
        if (event.getScreen() instanceof RefractionScreen) {
            RefractionClientMEvents.onRemove(event.getScreen());
        }
    }

    @SubscribeEvent
    public static void clientTick(TickEvent.ClientTickEvent event) {
        RefractionClientMEvents.clientTick(event.phase.equals(TickEvent.Phase.END));
    }

    @SubscribeEvent
    public static void attackEvent(InputEvent.InteractionKeyMappingTriggered event) {
        if (event.isAttack()) {
            event.setCanceled(RefractionClientMEvents.onAttack());
        }
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void registerOverlays(RegisterGuiOverlaysEvent event) {
        event.registerAboveAll("quest", (gui, guiGraphics, partialTicks, x, y) -> QuestRenderer.quest(guiGraphics, partialTicks));
        event.registerAboveAll("cinematic", (gui, guiGraphics, partialTicks, x, y) -> CinematicBars.bars(guiGraphics, partialTicks));
    }
}
