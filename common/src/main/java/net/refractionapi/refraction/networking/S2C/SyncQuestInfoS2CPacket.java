package net.refractionapi.refraction.networking.S2C;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.world.entity.player.Player;
import net.refractionapi.refraction.networking.Packet;
import net.refractionapi.refraction.quest.client.ClientQuestInfo;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.function.Consumer;

public class SyncQuestInfoS2CPacket extends Packet {

    private final boolean inQuest;
    private final Component questName;
    private final Component description;
    private final List<Component> partDescription;
    private final CompoundTag tag;

    public SyncQuestInfoS2CPacket(boolean inQuest, Component questName, Component description, List<Component> partDescription, CompoundTag tag) {
        this.inQuest = inQuest;
        this.questName = questName;
        this.description = description;
        this.partDescription = partDescription;
        this.tag = tag;
    }

    public SyncQuestInfoS2CPacket(FriendlyByteBuf buf) {
        this.inQuest = buf.readBoolean();
        this.questName = (Component) FormattedText.of(buf.readUtf());
        this.description = (Component) FormattedText.of(buf.readUtf());
        this.partDescription = buf.readList((buf1) -> (Component) FormattedText.of(buf1.readUtf()));
        this.tag = buf.readNbt();
    }

    @Override
    public void write(FriendlyByteBuf buf) {
        buf.writeBoolean(this.inQuest);
        buf.writeUtf(this.questName.getString());
        buf.writeUtf(this.description.getString());
        buf.writeCollection(this.partDescription, (buf1, component) -> buf1.writeUtf(component.getString()));
        buf.writeNbt(this.tag);
    }

    @Override
    public void handle(@Nullable Player player, Consumer<Runnable> context) {
        context.accept(() -> ClientQuestInfo.setQuestInfo(this.inQuest, this.questName, this.description, this.partDescription, this.tag));
    }

}
