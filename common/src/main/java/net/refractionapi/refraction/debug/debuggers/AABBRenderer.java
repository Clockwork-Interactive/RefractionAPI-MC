package net.refractionapi.refraction.debug.debuggers;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.datafixers.util.Pair;
import net.minecraft.Util;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.phys.AABB;
import net.refractionapi.refraction.debug.RDebugRenderer;
import net.refractionapi.refraction.helper.data.DoubleMap;

import java.awt.*;

public class AABBRenderer extends RDebugRenderer {
    private final DoubleMap<AABB, Color, Pair<Long, Integer>> boxes = new DoubleMap<>();

    public AABBRenderer() {
        super("aabb");
    }

    @Override
    protected void render(PoseStack poseStack, MultiBufferSource multiBufferSource) {
        this.boxes.removeIf((color, time) -> time.getFirst() + (time.getSecond() * 1000L) < Util.getMillis());
        this.boxes.forEach((box, color, time) -> renderLineBox(applyTransformation(box), color.getRed() / 255.0F, color.getGreen() / 255.0F, color.getBlue() / 255.0F, 1.0F, poseStack, multiBufferSource));
    }

    @Override
    protected void tick(boolean post) {
    }

    @Override
    protected void fromPacket(FriendlyByteBuf buf) {
        BlockPos corner1 = buf.readBlockPos();
        BlockPos corner2 = buf.readBlockPos();
        int red = buf.readInt();
        int green = buf.readInt();
        int blue = buf.readInt();
        int time = buf.readInt();
        AABB box = new AABB(corner1.getCenter(), corner2.getCenter());
        this.boxes.put(box, new Color(red, green, blue), Pair.of(Util.getMillis(), time));
    }
}
