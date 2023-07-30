package net.refractionapi.refraction.runnable;

import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.refractionapi.refraction.Refraction;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

@Mod.EventBusSubscriber(modid = Refraction.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class RunnableHandler {
    public static HashMap<Runnable, Integer> runnables = new HashMap<>();

    @SubscribeEvent
    public static void serverTick(TickEvent.ServerTickEvent event) {
        if (event.phase.equals(TickEvent.Phase.END)) return;
        Iterator<Map.Entry<Runnable, Integer>> iterator = runnables.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<Runnable, Integer> entry = iterator.next();
            Runnable runnable = entry.getKey();

            if (entry.getValue() == null || entry.getValue() < 0) {
                iterator.remove();
            } else {
                try {
                    entry.getKey().run();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                runnables.put(runnable, entry.getValue() - 1);
            }
        }
    }

    /**
     * Executes a runnable continuously for the given duration. <br>
     * Should only be called from the server side!
     *
     * @param runnable Runnable that should be executed.
     * @param duration Duration of how long the runnable should be executed for in ticks.
     */
    public static void addRunnable(Runnable runnable, int duration) {
        runnables.put(runnable, duration);
    }

}
