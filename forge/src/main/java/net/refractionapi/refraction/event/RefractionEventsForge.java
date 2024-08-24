package net.refractionapi.refraction.event;

import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.server.ServerStoppingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.refractionapi.refraction.Refraction;
import net.refractionapi.refraction.events.RefractionEvents;
import net.refractionapi.refraction.networking.RefractionMessages;
import net.refractionapi.refraction.platform.RefractionServices;

@Mod.EventBusSubscriber(modid = Refraction.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class RefractionEventsForge implements RefractionEvents {

    @SubscribeEvent
    public static void playerTick(TickEvent.PlayerTickEvent event) {
        RefractionCommonData.playerTick(event.player);
    }

    @SubscribeEvent
    public static void livingTick(LivingEvent.LivingTickEvent event) {
        RefractionCommonData.livingTick(event.getEntity());;
    }

    @SubscribeEvent
    public static void death(LivingDeathEvent event) {
        RefractionCommonData.death(event.getEntity(), event.getSource());
    }

    @SubscribeEvent
    public static void serverTick(TickEvent.ServerTickEvent event) {
        RefractionServices.EVENTS.serverTick(event.phase == TickEvent.Phase.END);
    }

    @SubscribeEvent
    public static void levelTick(TickEvent.LevelTickEvent event) {
        RefractionServices.EVENTS.levelTick(event.level, event.phase == TickEvent.Phase.END);
    }

    @SubscribeEvent
    public static void serverStopping(ServerStoppingEvent event) {
        RefractionServices.EVENTS.serverStopping();
    }

}
