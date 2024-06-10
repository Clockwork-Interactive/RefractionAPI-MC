package net.refractionapi.refraction.quest;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.refractionapi.refraction.Refraction;
import net.refractionapi.refraction.quest.points.KillPoint;

import java.util.HashMap;
import java.util.UUID;

@Mod.EventBusSubscriber(modid = Refraction.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class QuestHandler {

    public static final HashMap<UUID, Quest> QUESTS = new HashMap<>();

    @SubscribeEvent
    public static void serverTick(TickEvent.ServerTickEvent event) {
        if (event.phase.equals(TickEvent.Phase.END)) return;
        QUESTS.values().forEach(Quest::tick);
        QUESTS.entrySet().removeIf(entry -> entry.getValue().removable);
    }

    @SubscribeEvent
    public static void onKill(LivingDeathEvent event) {
        if (event.getSource().getEntity() instanceof ServerPlayer player) {
            if (QUESTS.containsKey(player.getUUID()))
                QUESTS.get(player.getUUID()).getQuestPoints().forEach(point -> {
                    if (point instanceof KillPoint killPoint) killPoint.onKill(event.getEntity());
                });
        }
    }

}
