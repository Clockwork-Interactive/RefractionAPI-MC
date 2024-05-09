package net.refractionapi.refraction.cutscenes.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.gui.GuiGraphics;

import java.awt.*;

public class CinematicBars {

    public static void renderBars(GuiGraphics guiGraphics, float partial) {
        if (!ClientCutsceneData.hasBars) return;
        int screenX = guiGraphics.guiWidth();
        int screenY = guiGraphics.guiHeight();
        float degrees = ClientCutsceneData.currentRot;
        PoseStack stack = guiGraphics.pose();
        stack.pushPose();
        stack.rotateAround(Axis.ZP.rotationDegrees(degrees), (float) screenX / 2, (float) screenY / 2, 0);
        guiGraphics.fill(-1000, -1000, screenX + 1000, ClientCutsceneData.currentBarHeight, Color.BLACK.getRGB());
        guiGraphics.fill(-1000, screenY + 1000, screenX + 1000, screenY - ClientCutsceneData.currentBarHeight, Color.BLACK.getRGB());
        stack.popPose();
    }

}
