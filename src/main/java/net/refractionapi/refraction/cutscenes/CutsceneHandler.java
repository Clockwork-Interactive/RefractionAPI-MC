package net.refractionapi.refraction.cutscenes;

import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.server.ServerStoppingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.refractionapi.refraction.Refraction;

import java.util.*;

@Mod.EventBusSubscriber(modid = Refraction.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class CutsceneHandler {
    public static final HashMap<Player, List<Cutscene>> QUEUE = new HashMap<>();

    public static boolean inCutscene(Player player) {
        return QUEUE.containsKey(player) && !QUEUE.get(player).isEmpty();
    }

    @SubscribeEvent
    public static void serverTick(TickEvent.ServerTickEvent event) {
        if (event.phase.equals(TickEvent.Phase.END)) return;

        Iterator<Map.Entry<Player, List<Cutscene>>> mapIterator = QUEUE.entrySet().iterator();

        while (mapIterator.hasNext()) {
            List<Cutscene> cutscenes = mapIterator.next().getValue();
            if (cutscenes.isEmpty()) {
                mapIterator.remove();
                continue;
            }
            Cutscene cutscene = cutscenes.get(0);
            if (cutscenes.size() > 1) {
                if (cutscene.ptr >= cutscene.time.length - 1 && cutscene.time[cutscene.time.length - 1] - 2 <= cutscene.progressTracker) {
                    cutscene.stop();
                    cutscenes.remove(0);
                    cutscene = cutscenes.get(0);
                    cutscene.start();
                    continue;
                }
            }
            if (cutscene == null) continue;
            if (cutscene.player.isDeadOrDying()) {
                Cutscene.stopAll(cutscene.player);
                continue;
            }
            if (!cutscene.started) {
                cutscene.start();
            }
            cutscene.tick();
            if (cutscene.stopped) {
                cutscenes.remove(0);
                cutscene.stop();
            }
        }

    }

    @SubscribeEvent
    public static void serverStoppingEvent(ServerStoppingEvent event) {
        QUEUE.forEach((player, cutscenes) -> {
            for (Cutscene cutscene : cutscenes) {
                cutscene.stop();
            }
        });
    }

}
