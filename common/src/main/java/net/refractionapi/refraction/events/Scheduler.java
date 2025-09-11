package net.refractionapi.refraction.events;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class Scheduler {
    private static final HashMap<Runnable, Boolean> tasks = new HashMap<>();

    public static void addTask(boolean post, Runnable task) {
        tasks.put(task, post);
    }

    private static void runAndDelete(boolean post) {
        Iterator<Map.Entry<Runnable, Boolean>> it = tasks.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<Runnable, Boolean> next = it.next();
            if (post == next.getValue()) {
                next.getKey().run();
                it.remove();
            }
        }
    }

    public static void init() {
        RefractionEvents.SERVER_TICK.register(Scheduler::runAndDelete);
    }
}
