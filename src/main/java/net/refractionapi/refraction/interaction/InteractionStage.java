package net.refractionapi.refraction.interaction;

import net.minecraft.network.chat.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

public class InteractionStage {

    protected final NPCInteraction npcInteraction;
    protected final String id;
    protected boolean ends;
    protected Component dialouge;
    protected int dialougeTicks;
    protected final HashMap<Component, buttonOptions> options = new HashMap<>();
    protected Consumer<NPCInteraction> onSwitch;

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
        this.options.put(component, new buttonOptions(goTo, Optional.empty()));
        return this;
    }

    public InteractionStage addOption(Component component, String goTo, Consumer<NPCInteraction> consumer) {
        this.options.put(component, new buttonOptions(goTo, Optional.ofNullable(consumer)));
        return this;
    }

    public InteractionStage newStage(String id) {
        return this.npcInteraction.newStage(id);
    }

    public InteractionStage onSwitch(Consumer<NPCInteraction> onSwitch) {
        this.onSwitch = onSwitch;
        return this;
    }

    public void onSwitch() {
        if (this.onSwitch != null) {
            this.onSwitch.accept(this.npcInteraction);
        }
    }

    public List<String> possibleGoTos() {
        return List.copyOf(this.options.values().stream().map(buttonOptions::goTo).toList());
    }

    public List<Component> possibleOptions() {
        return List.copyOf(this.options.keySet());
    }

    public InteractionStage end() {
        this.ends = true;
        return this;
    }

    public boolean ends() {
        return this.ends;
    }

    public boolean shouldInstantlyClose() {
        return this.dialougeTicks == -1;
    }

    public Component getDialouge() {
        return this.dialouge;
    }

    public int getDialougeTicks() {
        return this.dialougeTicks;
    }

    public String getOption(Component component) {
        return this.options.get(component).goTo();
    }

    public HashMap<Component, buttonOptions> getOptions() {
        return this.options;
    }

    public record buttonOptions(String goTo, Optional<Consumer<NPCInteraction>> onClick) {

    }

}
