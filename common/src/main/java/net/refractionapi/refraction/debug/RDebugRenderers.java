package net.refractionapi.refraction.debug;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.phys.AABB;
import net.refractionapi.refraction.networking.RefractionMessages;
import net.refractionapi.refraction.networking.S2C.DebugRendererS2CPacket;

public class RDebugRenderers {

    public static void renderAABB(AABB aabb, int r, int g, int b, int time, ServerLevel level) {
        CompoundTag tag = new CompoundTag();
        BlockPos corner1 = BlockPos.containing(aabb.minX, aabb.minY, aabb.minZ);
        BlockPos corner2 = BlockPos.containing(aabb.maxX, aabb.maxY, aabb.maxZ);
        tag.putLong("blockpos1", corner1.asLong());
        tag.putLong("blockpos2", corner2.asLong());
        tag.putInt("red", r);
        tag.putInt("green", g);
        tag.putInt("blue", b);
        tag.putInt("time", time);
        send("aabb", tag, level);
    }

    public static void send(String id, CompoundTag tag, ServerLevel level) {
        RefractionMessages.sendToAll(new DebugRendererS2CPacket(id, tag), level);
    }

}
