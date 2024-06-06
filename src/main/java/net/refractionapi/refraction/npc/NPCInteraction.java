package net.refractionapi.refraction.npc;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;

import java.util.HashMap;

public abstract class NPCInteraction {

    private final LivingEntity entity;
    private InteractionStage firstStage;
    private final HashMap<String, InteractionStage> stages = new HashMap<>();

    public NPCInteraction(LivingEntity entity) {
        this.entity = entity;
        this.init();
    }

    public abstract void init();


    public void start(Player player) {

    }

    public void close(Player player) {

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
