package net.refractionapi.refraction.debug;

import it.unimi.dsi.fastutil.longs.LongSet;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.pathfinder.Path;
import net.minecraft.world.phys.AABB;
import net.refractionapi.refraction.helper.vec3.RAAB;

import java.util.Set;

public interface IRDebugRenderers{
    void renderAABB(AABB aabb, int r, int g, int b, int time);

    void renderRAAB(RAAB aabb, int r, int g, int b, int time);

    void renderPath(int entityId, Path path, float maxDistance);

    void updateForcedChunks(LongSet pos);

    void send(String id, FriendlyByteBuf buf);
}
