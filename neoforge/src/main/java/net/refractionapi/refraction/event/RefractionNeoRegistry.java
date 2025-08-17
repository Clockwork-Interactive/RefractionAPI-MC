package net.refractionapi.refraction.event;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.event.InputEvent;
import net.neoforged.neoforge.client.event.ScreenEvent;
import net.refractionapi.refraction.Refraction;
import net.refractionapi.refraction.events.event.RefractionClientMEvents;
import net.refractionapi.refraction.feature.examples.rendering.NVGArmor;
import net.refractionapi.refraction.feature.examples.rendering.RExampleLayers;
import net.refractionapi.refraction.feature.screen.RefractionScreen;

@EventBusSubscriber(modid = Refraction.MOD_ID, value = Dist.CLIENT, bus = EventBusSubscriber.Bus.MOD)
public class RefractionNeoRegistry {
    @SubscribeEvent
    public static void registerLayer(EntityRenderersEvent.RegisterLayerDefinitions event) {
        event.registerLayerDefinition(RExampleLayers.NVG, NVGArmor::createBodyLayer);
    }
}
