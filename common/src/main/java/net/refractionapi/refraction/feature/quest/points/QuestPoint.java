package net.refractionapi.refraction.feature.quest.points;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.refractionapi.refraction.feature.quest.Quest;

import java.util.function.Consumer;

public abstract class QuestPoint {

    protected Quest quest;
    protected boolean completed = false;
    private boolean loaded = false;
    private Consumer<CompoundTag> saveAdditional;
    private Consumer<CompoundTag> onDeserialize;

    public QuestPoint(Quest quest) {
        this.quest = quest;
    }

    public abstract void tick();

    public abstract Component description();

    public abstract String id();

    public String genericID() {
        return this.getClass().getSimpleName();
    }

    public boolean checkCompletion() {
        return this.completed;
    }

    /**
     * Prevents duplicate loading of the same point, if id is the same.
     */
    public boolean loaded() {
        return this.loaded;
    }

    public void serialize(CompoundTag tag) {
        tag.putBoolean("completed", this.completed);
        tag.putString("id", this.id());
        tag.putString("genericID", this.genericID());
        if (this.saveAdditional != null) {
            this.saveAdditional.accept(tag);
        }
    }

    public void deserialize(CompoundTag tag) {
        this.completed = tag.getBoolean("completed");
        this.loaded = true;
        if (this.onDeserialize != null) {
            this.onDeserialize.accept(tag);
        }
    }

    public QuestPoint saveAdditional(Consumer<CompoundTag> consumer) {
        this.saveAdditional = consumer;
        return this;
    }

    public QuestPoint onDeserialize(Consumer<CompoundTag> consumer) {
        this.onDeserialize = consumer;
        return this;
    }

}
