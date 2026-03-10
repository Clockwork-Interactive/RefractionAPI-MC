package net.refractionapi.refraction.debug;

import io.netty.buffer.Unpooled;
import it.unimi.dsi.fastutil.longs.LongSet;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.pathfinder.Node;
import net.minecraft.world.level.pathfinder.Path;
import net.minecraft.world.level.pathfinder.Target;
import net.minecraft.world.phys.AABB;
import net.refractionapi.refraction.Refraction;
import net.refractionapi.refraction.config.RRuntimeConfig;
import net.refractionapi.refraction.config.RServerConfig;
import net.refractionapi.refraction.feature.channel.NamedAPI;
import net.refractionapi.refraction.helper.vec3.RAAB;
import net.refractionapi.refraction.mixininterfaces.IPath;

import java.util.Set;
import java.util.stream.Collectors;

public class RDebugRenderers implements IRDebugRenderers {
    private static IRDebugRenderers instance;
    private static final RDebugEmpty empty = new RDebugEmpty();
    public static final ResourceLocation API_ID = Refraction.id("debug");
    private static final NamedAPI api = NamedAPI.create(API_ID).configure((channel) -> {
        channel.canSendTo(RServerConfig::isPermitted);
    }).initOnServerStart();

    private RDebugRenderers() {

    }

    public void renderAABB(AABB aabb, int r, int g, int b, int time) {
        FriendlyByteBuf buf = new FriendlyByteBuf(Unpooled.buffer());
        BlockPos corner1 = BlockPos.containing(aabb.minX, aabb.minY, aabb.minZ);
        BlockPos corner2 = BlockPos.containing(aabb.maxX, aabb.maxY, aabb.maxZ);
        buf.writeBlockPos(corner1);
        buf.writeBlockPos(corner2);
        buf.writeInt(r);
        buf.writeInt(g);
        buf.writeInt(b);
        buf.writeInt(time);
        send("aabb", buf);
    }

    public void renderRAAB(RAAB aabb, int r, int g, int b, int time) {
        CompoundTag tag = new CompoundTag();
    }

    @Override
    public void renderPath(int entityId, Path path, float maxDistance) {
        if (path == null) return;
        if (path instanceof IPath iPath)
            iPath.debug(iPath.getNodes().toArray(new Node[0]), iPath.getNodes().toArray(new Node[0]), iPath.getNodes().stream().map((node -> new Target(node.x, node.y, node.z))).collect(Collectors.toSet()));
        FriendlyByteBuf buf = new FriendlyByteBuf(Unpooled.buffer());
        buf.writeInt(entityId);
        buf.writeFloat(maxDistance);
        path.writeToStream(buf);
        send("pathfinding", buf);
    }

    @Override
    public void updateForcedChunks(LongSet positions) {
        FriendlyByteBuf buf = new FriendlyByteBuf(Unpooled.buffer());
        buf.writeInt(positions.size());
        positions.forEach(buf::writeLong);
        send("forced_chunks", buf);
    }

    public void send(String id, FriendlyByteBuf buf) {
        if (api.channel() == null || api.channel().isClosed()) return; // should never be the case --Zeus
        api.channel().send("route", (send) -> {
            send.writeUtf(id);
            send.writeBytes(buf);
        });
    }

    public static IRDebugRenderers instance() {
        return RRuntimeConfig.debugTools ? instance == null ? instance = new RDebugRenderers() : instance : empty;
    }

    public static void init() {
    }
}
