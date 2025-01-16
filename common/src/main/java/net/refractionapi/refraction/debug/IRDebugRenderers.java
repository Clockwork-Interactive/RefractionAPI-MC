package net.refractionapi.refraction.debug;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.pathfinder.Path;
import net.minecraft.world.phys.AABB;
import net.refractionapi.refraction.helper.vec3.RAAB;

public interface IRDebugRenderers{
    void renderAABB(AABB aabb, int r, int g, int b, int time, ServerLevel level);

    void renderRAAB(RAAB aabb, int r, int g, int b, int time, ServerLevel level);

    void renderPath(int entityId, Path path, float maxDistance, ServerLevel level);

    void send(String id, FriendlyByteBuf buf, ServerLevel serverLevel);
}
