package net.refractionapi.refraction.client.rendering;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import net.minecraft.client.renderer.GameRenderer;
import org.joml.Matrix4f;

import java.awt.*;

public class RenderHelper {

    public static void renderLine(double startX, double startY, double endX, double endY, double width, Color color) {
        RenderSystem.setShader(GameRenderer::getPositionColorShader);
        Tesselator tessellator = Tesselator.getInstance();
        BufferBuilder bufferbuilder = tessellator.getBuilder();
        bufferbuilder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR);

        double changeInX = startX - endX;
        double changeInY = startY - endY;
        double angle = Math.atan(changeInY / changeInX);
        double pi2 = Math.PI / 2;

        final double xWidth = width * Math.cos(angle + pi2);
        final double yWidth = width * Math.sin(angle + pi2);

        if (changeInX >= 0) {
            bufferbuilder.vertex(startX + xWidth, startY + yWidth, 0).color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).endVertex();
            bufferbuilder.vertex(startX - xWidth, startY - yWidth, 0).color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).endVertex();
            bufferbuilder.vertex(endX - xWidth, endY - yWidth, 0).color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).endVertex();
            bufferbuilder.vertex(endX + xWidth, endY + yWidth, 0).color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).endVertex();
        } else {
            bufferbuilder.vertex(startX - xWidth, startY - yWidth, 0).color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).endVertex();
            bufferbuilder.vertex(startX + xWidth, startY + yWidth, 0).color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).endVertex();
            bufferbuilder.vertex(endX + xWidth, endY + yWidth, 0).color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).endVertex();
            bufferbuilder.vertex(endX - xWidth, endY - yWidth, 0).color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).endVertex();
        }

        tessellator.end();
    }

    /**
     *  Same as {@link net.minecraft.client.gui.GuiGraphics#fill(int, int, int, int, int)}, but uses {@link Color} instead of integers.
     */
    public static void fill(PoseStack stack, int minX, int minY, int maxX, int maxY, Color color) {
        Matrix4f matrix4f = stack.last().pose();
        if (minX < maxX) {
            int i = minX;
            minX = maxX;
            maxX = i;
        }

        if (minY < maxY) {
            int j = minY;
            minY = maxY;
            maxY = j;
        }

        BufferBuilder bufferbuilder = Tesselator.getInstance().getBuilder();
        RenderSystem.enableBlend();
        RenderSystem.setShader(GameRenderer::getPositionColorShader);
        bufferbuilder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR);
        bufferbuilder.vertex(matrix4f, (float)minX, (float)minY, 0).color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).endVertex();
        bufferbuilder.vertex(matrix4f, (float)minX, (float)maxY, 0).color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).endVertex();
        bufferbuilder.vertex(matrix4f, (float)maxX, (float)maxY, 0).color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).endVertex();
        bufferbuilder.vertex(matrix4f, (float)maxX, (float)minY, 0).color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).endVertex();
        BufferUploader.drawWithShader(bufferbuilder.end());
        RenderSystem.disableBlend();
    }

}
