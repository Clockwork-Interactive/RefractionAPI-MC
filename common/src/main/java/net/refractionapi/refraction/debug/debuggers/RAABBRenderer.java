package net.refractionapi.refraction.debug.debuggers;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.datafixers.util.Pair;
import net.minecraft.Util;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.refractionapi.refraction.debug.RDebugRenderer;
import net.refractionapi.refraction.helper.data.DoubleMap;

import java.awt.*;

public class RAABBRenderer extends RDebugRenderer {

    private final DoubleMap<AABB, Color, Pair<Long, Integer>> boxes = new DoubleMap<>();

    public RAABBRenderer() {
        super("aabb");
    }

    @Override
    protected void render(PoseStack poseStack, MultiBufferSource multiBufferSource) {
        Vec3 camera = this.cameraPosition();
        this.boxes.removeIf((color, time) -> time.getFirst() + (time.getSecond() * 1000L) < Util.getMillis());
        this.boxes.forEach((box, color, time) -> {
            AABB offsetBounds = new AABB(box.minX - camera.x - 0.51F, box.minY - camera.y - 0.51F, box.minZ - camera.z - 0.51F, box.maxX - camera.x + 0.51F, box.maxY - camera.y + 0.51F, box.maxZ - camera.z + 0.51F);
            renderBox(offsetBounds, color.getRed() / 255.0F, color.getGreen() / 255.0F, color.getBlue() / 255.0F, 1.0F, poseStack, multiBufferSource);
        });
    }

    @Override
    protected void tick(boolean post) {
    }

    @Override
    protected void fromPacket(CompoundTag tag) {
        BlockPos corner1 = BlockPos.of(tag.getLong("blockpos1"));
        BlockPos corner2 = BlockPos.of(tag.getLong("blockpos2"));
        int red = tag.getInt("red");
        int green = tag.getInt("green");
        int blue = tag.getInt("blue");
        int time = tag.getInt("time");
        AABB box = new AABB(corner1.getCenter(), corner2.getCenter());
        this.boxes.put(box, new Color(red, green, blue), Pair.of(Util.getMillis(), time));
    }

}
