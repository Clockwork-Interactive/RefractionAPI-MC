package net.refractionapi.refraction.quest.client;

import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;

import java.awt.*;

public class QuestRenderer {

    public static void quest(GuiGraphics guiGraphics, DeltaTracker partial) {
        if (!ClientQuestInfo.defaultRenderer || !ClientQuestInfo.inQuest) return;

        int screenWidth = guiGraphics.guiWidth();
        int screenHeight = guiGraphics.guiHeight();
        Font font = Minecraft.getInstance().font;

        int offset = ClientQuestInfo.description.getString().length() + 100;
        int yMax = 0;

        guiGraphics.drawCenteredString(font, ClientQuestInfo.questName, screenWidth - offset, 80, Color.WHITE.getRGB());
        guiGraphics.drawCenteredString(font, ClientQuestInfo.description, screenWidth - offset, 95, Color.WHITE.getRGB());
        for (int i = 0; i < ClientQuestInfo.partDescription.size(); i++) {
            yMax = 120 + (i * 10);
            guiGraphics.drawCenteredString(font, ClientQuestInfo.partDescription.get(i), screenWidth - offset, yMax, Color.WHITE.getRGB());
        }
        guiGraphics.fill(screenWidth - offset - 100, 70, screenWidth - offset / 2 + 45, yMax + 20, new Color(0, 0, 0, 70).getRGB());

    }

}
