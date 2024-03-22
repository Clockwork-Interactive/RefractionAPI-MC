package net.refractionapi.refraction.event;

import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterGuiOverlaysEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.refractionapi.refraction.Refraction;
import net.refractionapi.refraction.cutscenes.client.CinematicBars;

import static net.refractionapi.refraction.event.RefractionClientEvents.overlays;

@Mod.EventBusSubscriber(modid = Refraction.MOD_ID, value = Dist.CLIENT,bus = Mod.EventBusSubscriber.Bus.MOD)
public class RefractionRegistry {

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void registerOverlays(RegisterGuiOverlaysEvent event) {
        event.registerAboveAll("cinematic", CinematicBars.BARS);
    }


    public static void addOverlayExclusion(ResourceLocation overlay) {
        overlays.add(overlay);
    }

}
