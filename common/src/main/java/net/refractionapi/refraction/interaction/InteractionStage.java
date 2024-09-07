package net.refractionapi.refraction.interaction;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.refractionapi.refraction.networking.C2S.SyncInteractionC2SPacket;
import net.refractionapi.refraction.networking.RefractionMessages;

import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

public class InteractionStage {

    protected final NPCInteraction npcInteraction;
    protected final String id;
    protected boolean ends;
    protected Component dialogue;
    protected int dialogueTicks;
    protected String goTo;
    protected final HashMap<Component, buttonOptions> options = new HashMap<>();
    protected Consumer<NPCInteraction> onSwitch = (i) -> {};

    public InteractionStage(NPCInteraction npcInteraction, String id) {
        this.npcInteraction = npcInteraction;
        this.id = id;
    }

    public String getId() {
        return this.id;
    }

    public InteractionStage addDialogue(Component component, int ticks, String goTo) {
        this.dialogue = component;
        this.dialogueTicks = ticks;
        this.goTo = goTo;
        return this;
    }

    public InteractionStage addDialogue(Component component, int ticks) {
        return this.addDialogue(component, ticks, "");
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

    public void onSwitch(String goTo, Component button) {
        this.onSwitch.accept(this.npcInteraction);
        CompoundTag tag = new CompoundTag();
        tag.putString("stage", goTo);
        tag.putString("button", Component.Serializer.toJson(button, this.npcInteraction.player.registryAccess()));
        RefractionMessages.sendToServer(new SyncInteractionC2SPacket(this.npcInteraction.getBuilder().getId(), tag));
    }

    public String getGoTo() {
        return this.goTo;
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
        return this.dialogueTicks == -1;
    }

    public Component getDialogue() {
        return this.dialogue;
    }

    public int getDialogueTicks() {
        return this.dialogueTicks;
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
