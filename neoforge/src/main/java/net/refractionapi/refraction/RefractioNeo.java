package net.refractionapi.refraction;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.refractionapi.refraction.networking.RefractionMessagesNeo;
import net.refractionapi.refraction.platform.RefractionServices;

@Mod(Refraction.MOD_ID)
public class RefractioNeo {

    public RefractioNeo(IEventBus eventBus) {
        Refraction.init();
        RefractionMessagesNeo.init(eventBus);
        if (RefractionServices.PLATFORM.isClient()) {
            RefractionServices.EVENTS.registerOverlays();
        }
    }

}