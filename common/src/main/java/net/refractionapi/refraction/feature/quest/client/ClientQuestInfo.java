package net.refractionapi.refraction.feature.quest.client;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.refractionapi.refraction.client.ClientData;

import java.util.List;

public class ClientQuestInfo {
    public static boolean defaultRenderer = false;
    public static boolean inQuest = false;
    public static Component questName;
    public static Component description;
    public static List<MutableComponent> partDescription;
    public static CompoundTag tag;

    public static void setQuestInfo(boolean inQuest, String questName, String description, List<String> partDescription, CompoundTag tag) {
        var registryAcccess = ClientData.getPlayer().registryAccess();
        ClientQuestInfo.inQuest = inQuest;
        ClientQuestInfo.questName = Component.Serializer.fromJson(questName, registryAcccess);
        ClientQuestInfo.description = Component.Serializer.fromJson(description, registryAcccess);
        ClientQuestInfo.partDescription = partDescription.stream()
                .map(s -> Component.Serializer.fromJson(s, registryAcccess))
                .toList();
        ClientQuestInfo.tag = tag;
    }

    public static void setDefaultRenderer(boolean renderer) {
        defaultRenderer = renderer;
    }

    public static void reset() {
        inQuest = false;
        questName = null;
        description = null;
        partDescription = null;
        tag = null;
    }
}
