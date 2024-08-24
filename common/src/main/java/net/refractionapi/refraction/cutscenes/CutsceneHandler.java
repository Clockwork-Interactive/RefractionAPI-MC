package net.refractionapi.refraction.cutscenes;

import net.minecraft.world.entity.LivingEntity;
import net.refractionapi.refraction.platform.RefractionServices;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class CutsceneHandler {

    public static final HashMap<LivingEntity, List<Cutscene>> QUEUE = new HashMap<>();

    public static boolean inCutscene(LivingEntity livingEntity) {
        return QUEUE.containsKey(livingEntity) && !QUEUE.get(livingEntity).isEmpty();
    }

    public static void init() {
        RefractionServices.EVENTS.addServerProcess(() -> {
            Iterator<Map.Entry<LivingEntity, List<Cutscene>>> mapIterator = QUEUE.entrySet().iterator();

            while (mapIterator.hasNext()) {
                Map.Entry<LivingEntity, List<Cutscene>> entry = mapIterator.next();
                List<Cutscene> cutscenes = entry.getValue();
                if (cutscenes.isEmpty()) {
                    mapIterator.remove();
                    continue;
                }
                Cutscene cutscene = cutscenes.get(0);
                if (cutscenes.size() > 1) {
                    if (cutscene.points.isEmpty()) {
                        cutscene.stop();
                        cutscenes.remove(0);
                        cutscene = cutscenes.get(0);
                        cutscene.start();
                        continue;
                    }
                }
                if (cutscene == null) continue;
                if (cutscene.livingEntity.isDeadOrDying()) {
                    Cutscene.stopAll(cutscene.livingEntity);
                    continue;
                }
                if (!cutscene.started) {
                    cutscene.start();
                }
                cutscene.tick();
                if (cutscene.stopped) {
                    if (cutscene.beforeStop != null)
                        cutscene.beforeStop.accept(cutscene);
                    cutscenes.remove(0);
                    cutscene.stop();
                }
            }
        });
        RefractionServices.EVENTS.addServerStoppingListener(() -> {
            QUEUE.forEach((player, cutscenes) -> {
                for (Cutscene cutscene : cutscenes) {
                    cutscene.stop();
                }
            });
        });
    }


}
