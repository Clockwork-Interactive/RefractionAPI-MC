package net.refractionapi.refraction.runnable;

import net.refractionapi.refraction.Refraction;
import net.refractionapi.refraction.events.RefractionEvents;

import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class RunnableCooldownHandler {

    public static final Map<Runnable, Integer> RUNNABLE_TICK_MAP = new ConcurrentHashMap<>();

    /**
     * Do note, since this depends on a delay to call, not all methods that are in the runnable is guaranteed to exist
     * once the runnable executes. <br>
     * This is intended to run on the server, or when on client on the logical server side, world.isClientSide doesn't
     * have to be checked <br><br>
     * NOTE: If the server closes before the event executes, the event will not occur and not reload when the server
     * loads, there is no way to save the runnable data unless we dumb it down a lot.
     */
    public static void addDelayedRunnable(Runnable runnable, int delayInTicks) {
        if (delayInTicks <= 0) {
            runnable.run();
        }
        RUNNABLE_TICK_MAP.put(runnable, delayInTicks);
    }

    /**
     * Used to clear the map when disconnecting/closing client/server it to avoid multiple errors
     */
    private static void clear() {
        RUNNABLE_TICK_MAP.clear();
        RunnableHandler.RUNNABLES.clear();
    }

    public static void init() {
        RefractionEvents.SERVER_TICK.register((post) -> {
            Iterator<Map.Entry<Runnable, Integer>> iterator = RUNNABLE_TICK_MAP.entrySet().iterator();
            while (iterator.hasNext()) {
                Map.Entry<Runnable, Integer> entry = iterator.next();
                Runnable runnable = entry.getKey();

                if (entry.getValue() == 0) {
                    try {
                        runnable.run();
                    } catch (Exception e) {
                        Refraction.LOGGER.error("Error while executing runnable: {}", runnable);
                    }
                    iterator.remove();
                    continue;
                }

                RUNNABLE_TICK_MAP.put(runnable, entry.getValue() - 1);
            }
        });
    }

}
