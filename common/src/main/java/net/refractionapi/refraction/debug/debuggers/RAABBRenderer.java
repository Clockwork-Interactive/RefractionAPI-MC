package net.refractionapi.refraction.debug.debuggers;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.refractionapi.refraction.debug.RDebugRenderer;
import net.refractionapi.refraction.helper.vec3.RAAB;
import net.refractionapi.refraction.helper.vec3.Vec3Helper;

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
        poseStack.popPose();
    }

    @Override
    protected void tick(boolean post) {

    }

    @Override
    protected void fromPacket(CompoundTag tag) {

    }

}
