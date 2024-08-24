package net.refractionapi.refraction.event;

import net.minecraft.resources.ResourceLocation;

import static net.refractionapi.refraction.event.RefractionClientEvents.overlays;
public class RefractionClientRegistry {

    public static void addOverlayExclusion(ResourceLocation overlay) {
        overlays.add(overlay);
    }

}
