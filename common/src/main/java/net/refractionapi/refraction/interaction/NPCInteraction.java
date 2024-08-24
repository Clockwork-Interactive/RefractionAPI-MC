package net.refractionapi.refraction.interaction;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.refractionapi.refraction.networking.RefractionMessages;
import net.refractionapi.refraction.networking.S2C.HandleInteractionS2CPacket;

import java.util.HashMap;

/**
 * There's quite a bit of boilerplate code to be written <br>
 * To look what to do with this, look at the interaction examples in the examples package <br>
 * Default commands that need to be implemented: <br>
 * close <br>
 * stage <br>
 * button <br>
 */
public abstract class NPCInteraction {

    protected final Player player;
    protected final InteractionBuilder<?> builder;
    protected InteractionStage firstStage;
    protected final HashMap<String, InteractionStage> stages = new HashMap<>();
    protected String currentStage;
    private boolean ended = false;

    public NPCInteraction(InteractionBuilder<?> builder, Player player) {
        this.builder = builder;
        this.player = player;
        this.init();
    }

    public abstract void init();

    public HashMap<String, InteractionStage> getStages() {
        return this.stages;
    }

    public InteractionStage getStage(String id) {
        return this.stages.get(id);
    }

    public InteractionStage firstStage() {
        return this.firstStage;
    }

    public InteractionBuilder<?> getBuilder() {
        return this.builder;
    }

    public void serialize(CompoundTag tag) {

    }

    public abstract void handle(CompoundTag tag);

    public boolean handleSwitch(CompoundTag tag) {
        String stage = tag.getString("stage");
        Component buttonUsed = tag.contains("button") ? Component.Serializer.fromJson(tag.getString("button")) : Component.empty();
        InteractionStage  current = this.getStage(this.currentStage);
        if (current == null) return false;
        if (!stage.isEmpty() && buttonUsed != null && !buttonUsed.getString().isEmpty() && (!current.possibleGoTos().contains(stage) || !current.possibleOptions().contains(buttonUsed)) && this.player instanceof ServerPlayer serverPlayer) {
            CompoundTag tagTo = new CompoundTag();
            tagTo.putString("stage", this.currentStage);
            RefractionMessages.sendToPlayer(new HandleInteractionS2CPacket(this.builder.getId(), tagTo), serverPlayer);
            return false;
        }
        current.getOptions().computeIfPresent(buttonUsed, (key, value) -> {
            value.onClick().ifPresent(consumer -> consumer.accept(this));
            return value;
        });
        current.onSwitch.accept(this);
        this.currentStage = tag.contains("stage") ? tag.getString("stage") : this.currentStage;
        InteractionStage stage1 = this.getStage(this.currentStage);
        this.ended = stage1 == null || stage1.ends();
        return true;
    }

    public boolean hasEnded() {
        return this.ended;
    }

    public void setEnded(boolean ended) {
        this.ended = ended;
    }

    public boolean stillValid() {
        return this.player.isAlive();
    }

    public Player getPlayer() {
        return this.player;
    }

    public void sendToServer(CompoundTag tag) {
        this.builder.sendToServer(this, tag);
    }

    public void sendToServer() {
        this.sendToServer(new CompoundTag());
    }

    public InteractionStage newStage(String id) {
        InteractionStage stage = new InteractionStage(this, id);
        if (this.firstStage == null) {
            this.firstStage = stage;
            this.currentStage = id;
        }
        this.stages.put(id, stage);
        return stage;
    }

}
