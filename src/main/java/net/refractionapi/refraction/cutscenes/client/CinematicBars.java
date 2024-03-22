package net.refractionapi.refraction.cutscenes.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraftforge.client.gui.overlay.IGuiOverlay;

import java.awt.*;

public class CinematicBars {


    public static IGuiOverlay BARS = ((gui, guiGraphics, partial, screenX, screenY) -> {

        if (!ClientCutsceneData.hasBars) return;

        float degrees = ClientCutsceneData.currentRot;

        PoseStack stack = guiGraphics.pose();

        stack.pushPose();

        stack.rotateAround(Axis.ZP.rotationDegrees(degrees), (float) screenX / 2, (float) screenY / 2, 0);

        guiGraphics.fill(-1000, -1000, screenX + 1000, ClientCutsceneData.currentBarHeight, Color.BLACK.getRGB());

        guiGraphics.fill(-1000, screenY + 1000, screenX + 1000, screenY - ClientCutsceneData.currentBarHeight, Color.BLACK.getRGB());

        stack.popPose();

    });

}
