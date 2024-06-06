package net.refractionapi.refraction.quest.points;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.refractionapi.refraction.quest.Quest;

public class KillPoint extends QuestPoint {

    private EntityType<?> entityType;
    private int amount;
    private int currentAmount = 0;

    public KillPoint(Quest quest, EntityType<?> entityType, int amount) {
        super(quest);
        this.entityType = entityType;
        this.amount = amount;
    }

    @Override
    public void tick() {

    }

    @Override
    public Component description() {
        return Component.literal("Kill: ").append(Component.translatable(this.entityType.getDescriptionId())).append(Component.literal(" %d/%d".formatted(this.currentAmount, this.amount)));
    }

    public void onKill(Entity entity) {
        if (entity.getType().equals(this.entityType)) {
            this.currentAmount++;
            if (this.currentAmount >= this.amount) {
                this.completed = true;
            }
        }
    }

    @Override
    public String id() {
        return "%s+%d".formatted(this.entityType.getDescriptionId(), this.amount);
    }

    @Override
    public void serialize(CompoundTag tag) {
        super.serialize(tag);
        tag.putString("entityType", EntityType.getKey(this.entityType).toString());
        tag.putInt("amount", this.amount);
        tag.putInt("currentAmount", this.currentAmount);
    }

    @Override
    public void deserialize(CompoundTag tag) {
        super.deserialize(tag);
        this.entityType = EntityType.byString(tag.getString("entityType")).orElseThrow(() -> new RuntimeException("Invalid entity type"));
        this.amount = tag.getInt("amount");
        this.currentAmount = tag.getInt("currentAmount");
    }

}
