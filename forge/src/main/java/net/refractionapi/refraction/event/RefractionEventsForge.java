package net.refractionapi.refraction.event;

import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.level.LevelEvent;
import net.minecraftforge.event.server.ServerStoppingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.refractionapi.refraction.Refraction;
import net.refractionapi.refraction.events.RefractionEvents;
import net.refractionapi.refraction.events.event.RefractionCommonData;

@Mod.EventBusSubscriber(modid = Refraction.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class RefractionEventsForge implements RefractionEvents {

    @SubscribeEvent
    public static void playerTick(TickEvent.PlayerTickEvent event) {
        RefractionCommonData.playerTick(event.player);
    }

    @SubscribeEvent
    public static void livingTick(LivingEvent.LivingTickEvent event) {
        RefractionCommonData.livingTick(event.getEntity());
    }

    @SubscribeEvent
    public static void death(LivingDeathEvent event) {
        RefractionCommonData.death(event.getEntity(), event.getSource());
    }

    @SubscribeEvent
    public static void serverTick(TickEvent.ServerTickEvent event) {
        RefractionEvents.SERVER_TICK.invoker().onTick(event.phase == TickEvent.Phase.END);
    }

    @SubscribeEvent
    public static void levelTick(TickEvent.LevelTickEvent event) {
        RefractionEvents.LEVEL_TICK.invoker().onTick(event.level, event.phase == TickEvent.Phase.END);
    }

    @SubscribeEvent
    public static void serverStopping(ServerStoppingEvent event) {
        RefractionEvents.SERVER_STOPPING.invoker().onStop();
    }

    @SubscribeEvent
    public static void loadLevel(LevelEvent.Load event) {
        RefractionEvents.LOAD_LEVEL.invoker().onLoad(event.getLevel());
    }

    @SubscribeEvent
    public static void playerJoin(PlayerEvent.PlayerLoggedInEvent event) {
        if (event.getEntity() instanceof ServerPlayer serverPlayer)
            RefractionEvents.PLAYER_JOINED.invoker().onJoin(serverPlayer);
    }

}
