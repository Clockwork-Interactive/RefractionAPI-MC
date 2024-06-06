package net.refractionapi.refraction.quest;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.refractionapi.refraction.Refraction;
import net.refractionapi.refraction.quest.points.KillPoint;

import java.util.HashMap;

@Mod.EventBusSubscriber(modid = Refraction.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class QuestHandler {

    public static final HashMap<LivingEntity, Quest> QUEUE = new HashMap<>();

    @SubscribeEvent
    public static void serverTick(TickEvent.ServerTickEvent event) {
        if (event.phase.equals(TickEvent.Phase.END)) return;
        QUEUE.values().forEach(Quest::tick);
        QUEUE.entrySet().removeIf(entry -> entry.getValue().removable);
    }

    @SubscribeEvent
    public static void onKill(LivingDeathEvent event) {
        if (event.getSource().getEntity() instanceof Player player) {
            if (QUEUE.containsKey(player))
                QUEUE.get(player).getQuestPoints().forEach(point -> {
                    if (point instanceof KillPoint killPoint) killPoint.onKill(event.getEntity());
                });
        }
    }

}
