package net.refractionapi.refraction.debug.debuggers;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.refractionapi.refraction.debug.RDebugRenderer;
import net.refractionapi.refraction.helper.math.ColorInterpolator;
import net.refractionapi.refraction.helper.vec3.RAAB;
import net.refractionapi.refraction.helper.vec3.Vec3Helper;

import java.awt.*;

public class RAABBRenderer extends RDebugRenderer {
    public RAABBRenderer() {
        super("raab");
    }

    @Override
    protected void render(PoseStack poseStack, MultiBufferSource multiBufferSource) {
        RAAB raab = RAAB.create(
                new Vec3(0, 0, 0),
                new Vec3(5, 4, 5)
        );
        poseStack.pushPose();
        Vec3 camera = this.cameraPosition();
        poseStack.translate(-camera.x, -camera.y, -camera.z);
        renderLineBox(raab, 0.0F, 0.0F, 1.0F, 1.0F, poseStack, multiBufferSource);
        Vec3 start = this.minecraft.player.position();
        AABB box = this.minecraft.player.getBoundingBox();
        if (advancedView && raab.intersects(box)) {
            raab.forCorners((i, end) -> {
                renderLine(start, end, 0.0F, 1.0F, 0.0F, 1.0F, 1.0F, poseStack, multiBufferSource);
                float[] angles = Vec3Helper.getDegreesBetweenPoints(start, end);
                renderText("%.1f | [%.1f ; %.1f]".formatted(start.distanceTo(end), angles[0], angles[1]), (start.add(end)), poseStack, multiBufferSource);
            });
        }
        final int colorStart = new Color(138, 27, 41).getRGB();
        final int colorMid = new Color(96, 37, 54).getRGB();
        final int colorEnd = new Color(53, 5, 21).getRGB();
        ColorInterpolator color = new ColorInterpolator()
                .addPoint(0xa3160b, 0xdb1e0f, 1.0F, 0.3F)
                .addPoint(0x360805, 1.0F, 0.8F);
        Color color1 = new Color(color.getColor(Minecraft.getInstance().player.tickCount % 100 / 100.0F));
        poseStack.popPose();
        renderBox(Minecraft.getInstance().player.getBoundingBox().move(3, 0, 0), (float) color1.getRed() / 255, (float) color1.getGreen() / 255, (float) color1.getBlue() / 255, 1.0F, poseStack, multiBufferSource);
    }

    @Override
    protected void tick(boolean post) {

    }

    @Override
    protected void fromPacket(FriendlyByteBuf buf) {

    }
}
