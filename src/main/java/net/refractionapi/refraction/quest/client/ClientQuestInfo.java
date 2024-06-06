package net.refractionapi.refraction.quest.client;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;

import java.util.List;

public class ClientQuestInfo {

    public static boolean defaultRenderer = false;
    public static boolean inQuest = false;
    public static Component questName;
    public static Component description;
    public static List<Component> partDescription;
    public static CompoundTag tag;

    public static void setQuestInfo(boolean inQuest, Component questName, Component description, List<Component> partDescription, CompoundTag tag) {
        ClientQuestInfo.inQuest = inQuest;
        ClientQuestInfo.questName = questName;
        ClientQuestInfo.description = description;
        ClientQuestInfo.partDescription = partDescription;
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
