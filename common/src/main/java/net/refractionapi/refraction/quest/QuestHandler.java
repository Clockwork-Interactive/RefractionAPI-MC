package net.refractionapi.refraction.quest;

import net.refractionapi.refraction.events.RefractionEvents;

import java.util.HashMap;
import java.util.UUID;

public class QuestHandler {

    public static final HashMap<UUID, Quest> QUESTS = new HashMap<>();

    public static void init() {
        RefractionEvents.SERVER_TICK.register((post) -> {
            QUESTS.values().forEach(Quest::tick);
            QUESTS.entrySet().removeIf(entry -> entry.getValue().removable);
        });
    }

}
