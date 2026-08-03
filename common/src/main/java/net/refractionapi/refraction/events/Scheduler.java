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
        var it = tasks.entrySet().iterator();
        while (it.hasNext()) {
            var next = it.next();
            if (post != next.getValue()) continue;
            next.getKey().run();
            it.remove();
        }
    }

    public static void init() {
        RefractionEvents.SERVER_TICK.register(Scheduler::runAndDelete);
    }
}
