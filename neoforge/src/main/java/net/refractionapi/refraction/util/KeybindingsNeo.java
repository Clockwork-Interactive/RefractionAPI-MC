package net.refractionapi.refraction.util;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;
import net.refractionapi.refraction.Refraction;

import java.util.ArrayList;
import java.util.List;

public class KeybindingsNeo implements Keybindings {

    private static final List<Mapping> mappings = new ArrayList<>();

    @Override
    public void register(Mapping mapping) {
        mappings.add(mapping);
    }

    @EventBusSubscriber(modid = Refraction.MOD_ID, value = Dist.CLIENT, bus = EventBusSubscriber.Bus.MOD)
    public static class Registrar {

        @SubscribeEvent
        public static void registerKeybinds(RegisterKeyMappingsEvent event) {
            for (Mapping mapping : mappings) {
                event.register(mapping.mapping());
            }
        }

    }

}
