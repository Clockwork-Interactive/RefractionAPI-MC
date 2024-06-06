package net.refractionapi.refraction.npc;

import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import org.apache.commons.lang3.tuple.Pair;

import java.util.HashMap;
import java.util.function.BiConsumer;

public class InteractionStage {

    private final NPCInteraction npcInteraction;
    private final String id;
    private Component dialouge;
    private int dialougeTicks;
    private final HashMap<Component, Pair<String, BiConsumer<NPCInteraction, Player>>> options = new HashMap<>();

    public InteractionStage(NPCInteraction npcInteraction, String id) {
        this.npcInteraction = npcInteraction;
        this.id = id;
    }

    public String getId() {
        return this.id;
    }

    public InteractionStage addDialouge(Component component, int ticks) {
        this.dialouge = component;
        this.dialougeTicks = ticks;
        return this;
    }

    public InteractionStage addOption(Component component, String goTo) {
        this.options.put(component, Pair.of(goTo, null));
        return this;
    }

    public InteractionStage addOption(Component component, String goTo, BiConsumer<NPCInteraction, Player> consumer) {
        this.options.put(component, Pair.of(goTo, consumer));
        return this;
    }

    public InteractionStage newStage(String id) {
        return this.npcInteraction.newStage(id);
    }

    public Component getDialouge() {
        return this.dialouge;
    }

    public int getDialougeTicks() {
        return this.dialougeTicks;
    }

    public String getOption(Component component) {
        return this.options.get(component).getLeft();
    }

    public BiConsumer<NPCInteraction, Player> getConsumer(Component component) {
        return this.options.get(component).getRight();
    }

    public void syncToClient() {

    }

}
