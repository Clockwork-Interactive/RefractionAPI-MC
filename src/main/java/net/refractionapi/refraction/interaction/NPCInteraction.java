package net.refractionapi.refraction.interaction;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.network.NetworkEvent;
import net.refractionapi.refraction.networking.RefractionMessages;
import net.refractionapi.refraction.networking.S2C.HandleInteractionS2CPacket;

import java.util.HashMap;

/**
 * There's quite a bit of boilerplate code to be written <br>
 * To look what to do with this, look at the interaction examples in the examples package <br>
 * Default commands that need to be implemented: <br>
 * close <br>
 * stage <br>
 */
public abstract class NPCInteraction {

    protected final Player player;
    protected final InteractionBuilder<?> builder;
    protected InteractionStage firstStage;
    protected final HashMap<String, InteractionStage> stages = new HashMap<>();
    protected String currentStage;

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

    public abstract void serialize(CompoundTag tag);

    public abstract void handle(CompoundTag tag);

    public boolean handleSwitch(CompoundTag tag) {
        if (tag.contains("stage") && !this.getStage(this.currentStage).possibleGoTos().contains(tag.getString("stage")) && this.player instanceof ServerPlayer serverPlayer) {
            CompoundTag tagTo = new CompoundTag();
            tagTo.putString("stage", this.currentStage);
            RefractionMessages.sendToPlayer(new HandleInteractionS2CPacket(this.builder.getId(), tagTo), serverPlayer);
            return false;
        }
        this.currentStage = tag.contains("stage") ? tag.getString("stage") : this.currentStage;
        return true;
    }

    public boolean stillValid() {
        return this.player.isAlive();
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
