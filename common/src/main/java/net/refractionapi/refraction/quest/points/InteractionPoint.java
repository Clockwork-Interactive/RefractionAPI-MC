package net.refractionapi.refraction.quest.points;

import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.refractionapi.refraction.quest.Quest;

public class InteractionPoint extends QuestPoint {

    private final EntityType<?> type;

    public InteractionPoint(Quest quest, EntityType<?> type) {
        super(quest);
        this.type = type;
    }

    @Override
    public void tick() {

    }

    @Override
    public String id() {
        return this.type.getDescriptionId();
    }

    @Override
    public Component description() {
        return Component.literal("Interact with ").append(Component.translatable(this.type.getDescriptionId()));
    }

    public void onInteract(Entity entity) {
        if (entity.getType().equals(this.type)) {
            this.completed = true;
        }
    }

}
