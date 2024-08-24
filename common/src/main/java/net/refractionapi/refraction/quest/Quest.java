package net.refractionapi.refraction.quest;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.refractionapi.refraction.Refraction;
import net.refractionapi.refraction.networking.RefractionMessages;
import net.refractionapi.refraction.networking.S2C.SyncQuestInfoS2CPacket;
import net.refractionapi.refraction.quest.points.QuestPoint;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;

public abstract class Quest {

    private final List<QuestPart> questParts = new ArrayList<>();
    private final UUID player;
    private ServerPlayer playerCache;
    private ServerLevel level;
    protected boolean removable = false;
    protected Consumer<Quest> onCompletion;

    public Quest(ServerPlayer player, CompoundTag tag) {
        this.player = player.getUUID();
        this.level = player.serverLevel();
        this.addToHandler();
        if (tag != null)
            this.deserializePreGen(tag);
        this.generate();
        if (tag != null)
            this.deserializeNBT(tag);
    }

    public abstract void generate();

    public abstract Component questName();

    public void addToHandler() {
        if (QuestHandler.QUESTS.containsKey(this.player)) {
            QuestHandler.QUESTS.get(this.player).end(false);
        }
        QuestHandler.QUESTS.put(this.player, this);
    }

    public void tick() {
        if (this.questParts.isEmpty()) {
            this.end(true);
            return;
        }
        QuestPart part = this.questParts.get(0);
        if (part.completed()) {
            if (part.getOnCompletion() != null)
                part.getOnCompletion().accept(part);
            this.questParts.remove(0);
        } else {
            part.tick();
        }
    }

    public QuestPart newPart(Component description) {
        QuestPart part = new QuestPart(this, description);
        this.questParts.add(part);
        return part;
    }

    public List<QuestPoint> getQuestPoints() {
        List<QuestPoint> points = new ArrayList<>();
        if (this.questParts.isEmpty()) return points;
        QuestPart part = this.questParts.get(0);
        return part.getQuestPoints();
    }

    public Quest onQuestEnd(Consumer<Quest> onCompletion) {
        this.onCompletion = onCompletion;
        return this;
    }

    public void end(boolean completed) {
        if (completed && this.onCompletion != null) {
            this.onCompletion.accept(this);
        }
        this.questParts.clear();
        RefractionMessages.sendToPlayer(new SyncQuestInfoS2CPacket(false, Component.empty(), Component.empty(), List.of(), new CompoundTag()), this.getPlayer());
        this.removable = true;
    }

    public ServerPlayer getPlayer() {
        ServerPlayer serverPlayer = (ServerPlayer) this.level.getPlayerByUUID(this.player);
        return this.playerCache = serverPlayer == null ? this.playerCache : serverPlayer;
    }

    public ServerLevel getLevel() {
        return this.level = this.getPlayer() == null ? this.level : this.getPlayer().serverLevel();
    }

    public boolean isCompleted() {
        return this.questParts.isEmpty();
    }

    public void serializeNBT(CompoundTag tag) {
        if (this.questParts.isEmpty()) return;
        CompoundTag questPartTag = new CompoundTag();
        QuestPart questPart = this.questParts.get(0);
        questPartTag.putInt("size", this.questParts.size());
        questPartTag.put("questPartSer", questPart.serialize());
        tag.put("questPart", questPartTag);
    }

    protected void deserializeNBT(CompoundTag tag) {
        if (!tag.contains("questPart")) return;
        CompoundTag questPartTag = tag.getCompound("questPart");
        int size = questPartTag.getInt("size");
        int difference = this.questParts.size() - size;
        if (difference > 0) {
            this.questParts.subList(0, difference).clear();
        }
        CompoundTag questPartTagSer = questPartTag.getCompound("questPartSer");
        this.questParts.get(0).deserialize(questPartTagSer);
    }

    protected void deserializePreGen(CompoundTag tag) {

    }

    public static <T extends Quest> T startQuest(Player player, Class<T> quest, CompoundTag tag) {
        if (!(player instanceof ServerPlayer serverPlayer)) return null;
        try {
            return quest.getConstructor(ServerPlayer.class, CompoundTag.class).newInstance(serverPlayer, tag);
        } catch (InvocationTargetException | InstantiationException | IllegalAccessException |
                 NoSuchMethodException e) {
            Refraction.LOGGER.error("Failed to create quest {}\n{}", quest, e);
        }
        return null;
    }

    public static <T extends Quest> T startQuest(Player player, Class<T> quest) {
        return startQuest(player, quest, new CompoundTag());
    }

}
