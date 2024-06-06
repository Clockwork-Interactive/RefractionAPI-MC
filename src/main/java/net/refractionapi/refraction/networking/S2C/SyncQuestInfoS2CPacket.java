package net.refractionapi.refraction.networking.S2C;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraftforge.network.NetworkEvent;
import net.refractionapi.refraction.networking.Packet;
import net.refractionapi.refraction.quest.client.ClientQuestInfo;

import java.util.List;

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
        this.questName = buf.readComponent();
        this.description = buf.readComponent();
        this.partDescription = buf.readList(FriendlyByteBuf::readComponent);
        this.tag = buf.readNbt();
    }

    @Override
    public void toBytes(FriendlyByteBuf buf) {
        buf.writeBoolean(this.inQuest);
        buf.writeComponent(this.questName);
        buf.writeComponent(this.description);
        buf.writeCollection(this.partDescription, FriendlyByteBuf::writeComponent);
        buf.writeNbt(this.tag);
    }

    @Override
    public void handle(NetworkEvent.Context context) {
        context.enqueueWork(() -> ClientQuestInfo.setQuestInfo(this.inQuest, this.questName, this.description, this.partDescription, this.tag));
        context.setPacketHandled(true);
    }

}
