package net.refractionapi.refraction.event;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.entity.living.LivingDeathEvent;
import net.neoforged.neoforge.event.entity.living.LivingEvent;
import net.neoforged.neoforge.event.server.ServerStoppingEvent;
import net.neoforged.neoforge.event.tick.LevelTickEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;
import net.neoforged.neoforge.event.tick.ServerTickEvent;
import net.refractionapi.refraction.events.RefractionEvents;
import net.refractionapi.refraction.platform.RefractionServices;

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
        RefractionServices.EVENTS.serverTick(false);
    }

    @SubscribeEvent
    public static void serverTick(ServerTickEvent.Post event) {
        RefractionServices.EVENTS.serverTick(true);
    }

    @SubscribeEvent
    public static void levelTickPre(LevelTickEvent.Pre event) {
        RefractionServices.EVENTS.levelTick(event.getLevel(), false);
        if (event.getLevel() instanceof ServerLevel serverLevel) {
            serverLevel.getAllEntities().forEach((e) -> {
                if (e instanceof LivingEntity entity) RefractionCommonData.livingTick(entity);
            });
        }
    }

    @SubscribeEvent
    public static void levelTickPost(LevelTickEvent.Post event) {
        RefractionServices.EVENTS.levelTick(event.getLevel(), true);
        if (event.getLevel() instanceof ServerLevel serverLevel) {
            serverLevel.getAllEntities().forEach((e) -> {
                if (e instanceof LivingEntity entity) RefractionCommonData.livingTick(entity);
            });
        }
    }

    @SubscribeEvent
    public static void serverStopping(ServerStoppingEvent event) {
        RefractionServices.EVENTS.serverStopping();
    }

}
