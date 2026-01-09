package net.refractionapi.refraction.networking.S2C;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.refractionapi.refraction.feature.quest.client.ClientQuestInfo;
import net.refractionapi.refraction.networking.Packet;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.function.Consumer;

public class SyncQuestInfoS2CPacket extends Packet {
    private final boolean inQuest;
    private final String questName;
    private final String description;
    private final List<String> partDescription;
    private final CompoundTag tag;

    public SyncQuestInfoS2CPacket(boolean inQuest, String questName, String description, List<String> partDescription, CompoundTag tag) {
        this.inQuest = inQuest;
        this.questName = questName;
        this.description = description;
        this.partDescription = partDescription;
        this.tag = tag;
    }

    public SyncQuestInfoS2CPacket(FriendlyByteBuf buf) {
        this.inQuest = buf.readBoolean();
        this.questName = buf.readUtf();
        this.description = buf.readUtf();
        this.partDescription = buf.readList(FriendlyByteBuf::readUtf);
        this.tag = buf.readNbt();
    }

    @Override
    public void write(FriendlyByteBuf buf) {
        buf.writeBoolean(this.inQuest);
        buf.writeUtf(this.questName);
        buf.writeUtf(this.description);
        buf.writeCollection(this.partDescription, FriendlyByteBuf::writeUtf);
        buf.writeNbt(this.tag);
    }

    @Override
    public void handle(@Nullable Player player, Consumer<Runnable> context) {
        context.accept(() -> ClientQuestInfo.setQuestInfo(this.inQuest, this.questName, this.description, this.partDescription, this.tag));
    }
}
