package net.refractionapi.refraction.util;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.phys.Vec3;
import net.refractionapi.refraction.Refraction;
import net.refractionapi.refraction.events.RefractionClientEvents;
import net.refractionapi.refraction.helper.misc.TagIO;

public class TestHooksClient {
    private final TagIO tagIO = new TagIO("testDir");

    public TestHooksClient() {
        this.onRenderPost();
        this.onPlayerJoin();
        this.onPlayerLeave();
    }

    public void onRenderPost() {
        RefractionClientEvents.POST_ALL.register(context -> {
            if (!Minecraft.getInstance().getDebugOverlay().showDebugScreen()) return;
            BlockPos testPos = new BlockPos(0, -60, 0);
            PoseStack poseStack = context.getPoseStack();
            Vec3 cameraPos = context.getCamera().getPosition();
            Vec3 distance = cameraPos.subtract(testPos.getX(), testPos.getY(), testPos.getZ()).scale(0.03F);
            poseStack.translate(testPos.getX() - cameraPos.x(), testPos.getY() - cameraPos.y(), testPos.getZ() - cameraPos.z());
            float distanceToCamera = (float) Math.abs(distance.length());
            poseStack.scale(distanceToCamera, distanceToCamera, distanceToCamera);
            poseStack.mulPose(context.getCamera().rotation().rotateY((float) Math.toRadians(90.0F)));
            Tesselator tesselator = Tesselator.getInstance();

            BufferBuilder consumer = tesselator.begin(VertexFormat.Mode.DEBUG_LINE_STRIP, DefaultVertexFormat.POSITION_COLOR_NORMAL);
            RenderSystem.setShader(GameRenderer::getPositionColorShader);
            RenderSystem.lineWidth(4.0F);
            RenderSystem.disableDepthTest();
            RenderSystem.disableCull();
            poseStack.pushPose();
            consumer.addVertex(poseStack.last().pose(), 0.0F, 1.0F, 0.0F).setColor(255, 0, 0, 255).setNormal(0,1, 1);
            consumer.addVertex(poseStack.last().pose(), 0.0F, 0.0F, 1.0F).setColor(255, 0, 0, 255).setNormal(0,1, 1);
            consumer.addVertex(poseStack.last().pose(), 0.0F, -1.0F, 0.0F).setColor(255, 0, 0, 255).setNormal(0,1, 1);
            consumer.addVertex(poseStack.last().pose(), 0.0F, 0.0F, -1.0F).setColor(255, 0, 0, 255).setNormal(0,1, 1);
            consumer.addVertex(poseStack.last().pose(), 0.0F, 1.0F, 0.0F).setColor(255, 0, 0, 255).setNormal(0,1, 1);
            poseStack.popPose();
            BufferUploader.drawWithShader(consumer.buildOrThrow());
            RenderSystem.enableDepthTest();
            RenderSystem.enableCull();
        });
    }

    public void onPlayerJoin() {
        RefractionClientEvents.CLIENT_PLAYER_JOIN.register(() -> {
            CompoundTag tag = new CompoundTag();
            tag.putString("test1", "test");
            tag.putDouble("test2", 0.0D);
            this.tagIO.save("test", tag);
        });
    }

    public void onPlayerLeave() {
        RefractionClientEvents.CLIENT_PLAYER_LEAVE.register(() -> {
            CompoundTag tag = this.tagIO.load("test");
            if (tag == null) return;
            Refraction.LOGGER.info(tag.toString());
        });
    }
}
