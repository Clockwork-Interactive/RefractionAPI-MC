package net.refractionapi.refraction.debug.debuggers;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.phys.Vec3;
import net.refractionapi.refraction.debug.RDebugRenderer;

import java.util.HashSet;
import java.util.Set;

public class ForcedChunkRenderer extends RDebugRenderer {
    final Set<ChunkPos> forcedChunks = new HashSet<>();

    public ForcedChunkRenderer() {
        super("forced_chunks");
    }

    @Override
    protected void render(PoseStack poseStack, MultiBufferSource multiBufferSource) {
        var level = Minecraft.getInstance().level;
        if (level == null) return;
        for (var chunkPos : forcedChunks) {
            int minY = level.getMinBuildHeight();
            int maxY = level.getMaxBuildHeight();
            int chunkXCenter = chunkPos.getBlockX(8);
            int chunkZCenter = chunkPos.getBlockZ(8);
            var start = new Vec3(chunkXCenter, minY, chunkZCenter);
            var end = new Vec3(chunkXCenter, maxY, chunkZCenter);
            var camPos = cameraPosition();
            poseStack.pushPose();
            poseStack.translate(-camPos.x, -camPos.y, -camPos.z);
            renderLine(
                    start, end,
                    1.0F, 1.0F, 1.0F, 1.0F,
                    10,
                    poseStack, multiBufferSource
            );
            poseStack.popPose();
        }
    }

    @Override
    protected void fromPacket(FriendlyByteBuf buf) {
        forcedChunks.clear();
        int size = buf.readInt();
        for (int i = 0; i < size; i++) forcedChunks.add(new ChunkPos(buf.readLong()));
    }
}
