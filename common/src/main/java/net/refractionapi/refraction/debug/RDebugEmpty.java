package net.refractionapi.refraction.debug;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.pathfinder.Path;
import net.minecraft.world.phys.AABB;
import net.refractionapi.refraction.helper.vec3.RAAB;

public class RDebugEmpty implements IRDebugRenderers{
    @Override
    public void renderAABB(AABB aabb, int r, int g, int b, int time, ServerLevel level) {

    }

    @Override
    public void renderRAAB(RAAB aabb, int r, int g, int b, int time, ServerLevel level) {

    }

    @Override
    public void renderPath(int entityId, Path path, float maxDistance, ServerLevel level) {

    }

    @Override
    public void send(String id, FriendlyByteBuf buf, ServerLevel serverLevel) {

    }
}
