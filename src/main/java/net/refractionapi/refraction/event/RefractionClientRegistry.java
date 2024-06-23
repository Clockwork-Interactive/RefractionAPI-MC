package net.refractionapi.refraction.event;

import net.minecraft.client.gui.LayeredDraw;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.refractionapi.refraction.Refraction;
import net.refractionapi.refraction.cutscenes.client.CinematicBars;
import net.refractionapi.refraction.event.events.RegisterGuiEvent;
import net.refractionapi.refraction.quest.client.QuestRenderer;

import static net.refractionapi.refraction.event.RefractionClientEvents.overlays;

@Mod.EventBusSubscriber(modid = Refraction.MOD_ID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.MOD)
public class RefractionClientRegistry {

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void registerOverlays(RegisterGuiEvent event) {
        event.registerNew(new LayeredDraw().add(CinematicBars::renderBars));
        event.registerNew(new LayeredDraw().add(QuestRenderer::renderQuest));
    }


    public static void addOverlayExclusion(ResourceLocation overlay) {
        overlays.add(overlay);
    }

}
