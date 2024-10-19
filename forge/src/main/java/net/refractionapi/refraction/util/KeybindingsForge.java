package net.refractionapi.refraction.util;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.refractionapi.refraction.Refraction;

import java.util.ArrayList;
import java.util.List;

public class KeybindingsForge implements Keybindings {

    private static final List<Mapping> mappings = new ArrayList<>();

    @Override
    public void register(Mapping mapping) {
        mappings.add(mapping);
    }

    @Mod.EventBusSubscriber(modid = Refraction.MOD_ID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.MOD)
    public static class Registrar {

        @SubscribeEvent
        public static void registerKeybinds(RegisterKeyMappingsEvent event) {
            for (Mapping mapping : mappings) {
                event.register(mapping.mapping());
            }
        }

    }

}
