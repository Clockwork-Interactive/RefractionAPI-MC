package net.refractionapi.refraction;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerWorldEvents;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.refractionapi.refraction.event.RefractionCommonData;
import net.refractionapi.refraction.events.RefractionEvents;
import net.refractionapi.refraction.networking.RefractionMessages;
import net.refractionapi.refraction.platform.RefractionServices;

public class RefractionFabric implements ModInitializer {

    @Override
    public void onInitialize() {
        Refraction.init();
        RefractionMessages.register();
        ServerTickEvents.START_SERVER_TICK.register(server -> {
            for (ServerPlayer player : server.getPlayerList().getPlayers()) {
                RefractionCommonData.playerTick(player);
            }
            RefractionEvents.SERVER_TICK.invoker().onTick(false);
        });
        ServerTickEvents.END_SERVER_TICK.register(server -> RefractionEvents.SERVER_TICK.invoker().onTick(true));
        ServerTickEvents.START_WORLD_TICK.register((ServerLevel world) -> {
            world.getAllEntities().forEach((e) -> {
                if (e instanceof LivingEntity) RefractionCommonData.livingTick((LivingEntity) e);
            });
            RefractionEvents.LEVEL_TICK.invoker().onTick(world, false);
        });
        ServerTickEvents.END_WORLD_TICK.register(world -> RefractionEvents.LEVEL_TICK.invoker().onTick(world, true));
        ServerLivingEntityEvents.AFTER_DEATH.register(RefractionCommonData::death);
        ServerLifecycleEvents.SERVER_STOPPING.register(server -> RefractionEvents.SERVER_STOPPING.invoker().onStop());
        ServerWorldEvents.LOAD.register((server, world) -> RefractionEvents.LOAD_LEVEL.invoker().onLoad(world));
    }

}
