package net.refractionapi.refraction.event;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingDeathEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.server.ServerStoppingEvent;
import net.neoforged.neoforge.event.tick.LevelTickEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;
import net.neoforged.neoforge.event.tick.ServerTickEvent;
import net.refractionapi.refraction.Refraction;
import net.refractionapi.refraction.events.RefractionEvents;
import net.refractionapi.refraction.events.event.RefractionCommonData;

@EventBusSubscriber(modid = Refraction.MOD_ID, bus = EventBusSubscriber.Bus.GAME)
public class RefractionEventsNeo implements RefractionEvents {

    @SubscribeEvent
    public static void playerTick(PlayerTickEvent event) {
        RefractionCommonData.playerTick(event.getEntity());
    }

    @SubscribeEvent
    public static void death(LivingDeathEvent event) {
        RefractionCommonData.death(event.getEntity(), event.getSource());
    }

    @SubscribeEvent
    public static void serverTick(ServerTickEvent.Pre event) {
        RefractionEvents.SERVER_TICK.invoker().onTick(false);
    }

    @SubscribeEvent
    public static void serverTick(ServerTickEvent.Post event) {
        RefractionEvents.SERVER_TICK.invoker().onTick(true);
    }

    @SubscribeEvent
    public static void levelTickPre(LevelTickEvent.Pre event) {
        RefractionEvents.LEVEL_TICK.invoker().onTick(event.getLevel(), false);
        if (event.getLevel() instanceof ServerLevel serverLevel) {
            serverLevel.getAllEntities().forEach((e) -> {
                if (e instanceof LivingEntity entity) RefractionCommonData.livingTick(entity);
            });
        }
    }

    @SubscribeEvent
    public static void levelTickPost(LevelTickEvent.Post event) {
        RefractionEvents.LEVEL_TICK.invoker().onTick(event.getLevel(), true);
        if (event.getLevel() instanceof ServerLevel serverLevel) {
            serverLevel.getAllEntities().forEach((e) -> {
                if (e instanceof LivingEntity entity) RefractionCommonData.livingTick(entity);
            });
        }
    }

    @SubscribeEvent
    public static void serverStopping(ServerStoppingEvent event) {
        RefractionEvents.SERVER_STOPPING.invoker().onStop();
    }

    @SubscribeEvent
    public static void playerJoin(PlayerEvent.PlayerLoggedInEvent event) {
        if (event.getEntity() instanceof ServerPlayer serverPlayer)
            RefractionEvents.PLAYER_JOINED.invoker().onJoin(serverPlayer);
    }

}
