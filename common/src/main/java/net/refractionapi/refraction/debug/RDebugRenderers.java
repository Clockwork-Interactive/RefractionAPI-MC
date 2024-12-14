package net.refractionapi.refraction.debug;

import io.netty.buffer.Unpooled;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.pathfinder.Node;
import net.minecraft.world.level.pathfinder.Path;
import net.minecraft.world.level.pathfinder.Target;
import net.minecraft.world.phys.AABB;
import net.refractionapi.refraction.Refraction;
import net.refractionapi.refraction.helper.vec3.RAAB;
import net.refractionapi.refraction.mixininterfaces.IPath;
import net.refractionapi.refraction.networking.RefractionMessages;
import net.refractionapi.refraction.networking.S2C.DebugRendererS2CPacket;

import java.util.stream.Collectors;

public class RDebugRenderers implements IRDebugRenderers {

    private static IRDebugRenderers instance;
    private static final RDebugEmpty empty = new RDebugEmpty();

    private RDebugRenderers() {

    }

    public void renderAABB(AABB aabb, int r, int g, int b, int time, ServerLevel level) {
        FriendlyByteBuf buf = new FriendlyByteBuf(Unpooled.buffer());
        BlockPos corner1 = BlockPos.containing(aabb.minX, aabb.minY, aabb.minZ);
        BlockPos corner2 = BlockPos.containing(aabb.maxX, aabb.maxY, aabb.maxZ);
        buf.writeBlockPos(corner1);
        buf.writeBlockPos(corner2);
        buf.writeInt(r);
        buf.writeInt(g);
        buf.writeInt(b);
        buf.writeInt(time);
        send("aabb", buf, level);
    }

    public void renderRAAB(RAAB aabb, int r, int g, int b, int time, ServerLevel level) {
        CompoundTag tag = new CompoundTag();
    }

    @Override
    public void renderPath(int entityId, Path path, float maxDistance, ServerLevel level) {
        if (path == null) return;
        if (path instanceof IPath iPath)
            iPath.debug(iPath.getNodes().toArray(new Node[0]), iPath.getNodes().toArray(new Node[0]), iPath.getNodes().stream().map((node -> new Target(node.x, node.y, node.z))).collect(Collectors.toSet()));
        FriendlyByteBuf buf = new FriendlyByteBuf(Unpooled.buffer());
        buf.writeInt(entityId);
        buf.writeFloat(maxDistance);
        path.writeToStream(buf);
        send("pathfinding", buf, level);
    }

    public void send(String id, FriendlyByteBuf buf, ServerLevel level) {
        for (ServerPlayer player : level.getPlayers((player) -> player.isCreative() || player.hasPermissions(2))) {
            RefractionMessages.sendToPlayer(new DebugRendererS2CPacket(id, buf), player);
        }
    }

    public static IRDebugRenderers instance() {
        return Refraction.debugTools ? instance == null ? instance = new RDebugRenderers() : instance : empty;
    }

}
