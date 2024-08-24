package net.refractionapi.refraction.quest;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.refractionapi.refraction.networking.RefractionMessages;
import net.refractionapi.refraction.networking.S2C.SyncQuestInfoS2CPacket;
import net.refractionapi.refraction.quest.points.QuestPoint;

import java.util.*;
import java.util.function.Consumer;

public class QuestPart {

    private final Quest quest;
    protected final Set<QuestPoint> questPoints = new HashSet<>();
    private final Component description;
    private List<Component> partDescription = new ArrayList<>();
    public CompoundTag tag;
    private Consumer<QuestPart> onCompletion;
    private Consumer<QuestPart> onTick;

    public QuestPart(Quest quest, Component description) {
        this.quest = quest;
        this.description = description;
    }

    public Quest getQuest() {
        return this.quest;
    }

    public ServerPlayer getPlayer() {
        return this.quest.getPlayer();
    }

    public Component getDescription() {
        return this.description;
    }

    public List<Component> getPartDescription() {
        return this.partDescription;
    }

    public Quest build() {
        return this.quest;
    }

    public List<QuestPoint> getQuestPoints() {
        return new ArrayList<>(this.questPoints);
    }

    public void tick() {
        this.questPoints.stream().filter(questPoint -> !questPoint.checkCompletion()).forEach(QuestPoint::tick);
        this.partDescription = this.questPoints.stream().filter(questPoint -> !questPoint.checkCompletion()).map(QuestPoint::description).sorted(Comparator.comparing(Component::getString)).toList();
        this.tag = this.serialize();
        if (this.onTick != null) {
            this.onTick.accept(this);
        }
        this.syncToClient();
    }

    public void syncToClient() {
        if (!this.quest.isCompleted())
            RefractionMessages.sendToPlayer(new SyncQuestInfoS2CPacket(!this.quest.isCompleted(), this.quest.questName(), this.description, this.partDescription, this.tag), this.quest.getPlayer());
    }

    public QuestPart newPart(Component description) {
        return this.quest.newPart(description);
    }

    public QuestPart onCompletion(Consumer<QuestPart> onCompletion) {
        this.onCompletion = onCompletion;
        return this;
    }

    public QuestPart onTick(Consumer<QuestPart> onTick) {
        this.onTick = onTick;
        return this;
    }

    public Consumer<QuestPart> getOnCompletion() {
        return this.onCompletion;
    }

    public boolean completed() {
        return this.questPoints.stream().allMatch(QuestPoint::checkCompletion);
    }

    public QuestPart addQuestPoint(QuestPoint questPoint) {
        this.questPoints.add(questPoint);
        return this;
    }

    public QuestPart removeQuestPoint(QuestPoint questPoint) {
        this.questPoints.remove(questPoint);
        return this;
    }

    public CompoundTag serialize() {
        CompoundTag tag = new CompoundTag();
        ListTag points = new ListTag();
        for (QuestPoint questPoint : this.questPoints) {
            CompoundTag point = new CompoundTag();
            questPoint.serialize(point);
            points.add(point);
        }
        tag.put("points", points);
        return tag;
    }

    public void deserialize(CompoundTag tag) {
        ListTag points = tag.getList("points", Tag.TAG_COMPOUND);
        for (Tag point : points) {
            String id = ((CompoundTag) point).getString("id");
            this.questPoints.stream().filter(questPoint -> !questPoint.loaded() && questPoint.id().equals(id)).findFirst().ifPresent(questPoint -> questPoint.deserialize((CompoundTag) point));
        }
    }

}