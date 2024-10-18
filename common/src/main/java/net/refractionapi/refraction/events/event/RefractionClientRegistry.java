package net.refractionapi.refraction.events.event;

import net.minecraft.resources.ResourceLocation;

import static net.refractionapi.refraction.events.event.RefractionClientEvents.overlays;
public class RefractionClientRegistry {

    public static void addOverlayExclusion(ResourceLocation overlay) {
        overlays.add(overlay);
    }

}
