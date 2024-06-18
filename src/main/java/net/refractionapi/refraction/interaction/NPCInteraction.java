package net.refractionapi.refraction.interaction;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;

import java.util.HashMap;

public abstract class NPCInteraction {

    protected final Player player;
    protected final InteractionBuilder<?> builder;
    protected InteractionStage firstStage;
    protected final HashMap<String, InteractionStage> stages = new HashMap<>();

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

    public abstract void serialize(CompoundTag tag);

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
        }
        this.stages.put(id, stage);
        return stage;
    }

}
