package net.refractionapi.refraction.quest;

import net.refractionapi.refraction.platform.RefractionServices;

import java.util.HashMap;
import java.util.UUID;

public class QuestHandler {

    public static final HashMap<UUID, Quest> QUESTS = new HashMap<>();

    public static void init() {
        RefractionServices.EVENTS.addServerProcess(() -> {
            QUESTS.values().forEach(Quest::tick);
            QUESTS.entrySet().removeIf(entry -> entry.getValue().removable);
        });
    }

}
