package net.refractionapi.refraction.feature.subdivision;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.Rotation;
import net.refractionapi.refraction.Refraction;
import net.refractionapi.refraction.helper.randomizer.WeightedRandom;
import net.refractionapi.refraction.helper.vec3.Vec3Helper;
import net.refractionapi.refraction.util.Tuple;

import java.util.Arrays;

public class SubdivisionGenerator {
    final MinecraftServer server;

    public SubdivisionGenerator(MinecraftServer server) {
        this.server = server;
    }

    public void generate(SubdivisionSet set, ServerLevel serverLevel, BlockPos center) {
        var origin = set.origin();
        if (origin == null) return;
        var originPiece = generateSingle(
                serverLevel,
                center,
                origin,
                0
        );
        if (originPiece == null) return;
        var toPlace = set.pieces();
        int maxDepth = set.maxDepth;
        for (int i = 0; i < maxDepth; i++) {
            var lastPiece = originPiece;
            int attempts = (lastPiece.struct.doors().size() - lastPiece.takenDoors.size()) * 2;
            for (int j = 0; j < attempts; j++) {
                var nextConfig = toPlace.get();
                var newPiece = place(serverLevel, lastPiece, nextConfig);
                lastPiece = newPiece == null ? lastPiece : newPiece;
            }
        }
    }

    protected SubdivisionPiece generateSingle(ServerLevel serverLevel, BlockPos center, SubdivisionPiece.Configurer piece, int rotationSteps) {
        var struct = get(piece.id);
        if (struct == null) return null;
        var size = struct.template().getSize();
        var spawn = center.offset(-size.getX() / 2, 0, -size.getZ() / 2);
        return addPiece(piece.factory.create(piece.id, serverLevel, null, spawn), null, rotationSteps);
    }

    private SubdivisionPiece place(ServerLevel serverLevel, SubdivisionPiece previous, SubdivisionPiece.Configurer piece) {
        var newStruct = get(piece.id);
        var previousStruct = previous.struct;
        if (newStruct == null) return null;
        // find matching (same size) door pair that hasn't been taken
        // <currDoor, prevDoor> --Zeus
        Tuple<SubdivisionPiece.Door, SubdivisionPiece.Door> doorPair = null;
        for (var door : newStruct.doors()) {
            if (doorPair != null) break;
            if (previous.isDoorTaken(door)) continue;
            for (var previousDoor : previousStruct.doors()) {
                if (previous.isDoorTaken(previousDoor)) continue;
                if (!Arrays.equals(door.size(), previousDoor.size())) continue;
                doorPair = new Tuple<>(door, previousDoor);
                break;
            }
        }
        if (doorPair == null) return null;
        var currDoor = doorPair.first();
        var prevDoor = doorPair.second();
        previous.occupyDoor(prevDoor);
        var currDoorDirection = currDoor.direction().get2DDataValue();
        var prevRotationSteps = previous.rotationSteps;
        var prevDoorDirection = (prevDoor.direction().get2DDataValue() + prevRotationSteps) % 4;
        var rotationSteps = (prevDoorDirection - currDoorDirection + 6) % 4;
        var prevSpawn = previous.spawn();
        // offset in the direction of the door, using struct sizes --Zeus
        var offset = Direction.from2DDataValue(prevDoorDirection).step();
        var newStructSize = newStruct.template().getSize();
        var blockOffset = BlockPos.containing(
                offset.x * newStructSize.getX(),
                offset.y * newStructSize.getY(),
                offset.z * newStructSize.getZ()
        );
        return addPiece(piece.factory.create(
                piece.id,
                serverLevel,
                previous,
                prevSpawn.offset(blockOffset)
        ), currDoor, rotationSteps);
    }

    private SubdivisionPiece addPiece(SubdivisionPiece piece, SubdivisionPiece.Door door, int rotationSteps) {
        piece.place(door, rotationSteps);
        return piece;
    }

    private Subdivision.StructCache get(ResourceLocation id) {
        return Subdivision.getInstance().get(id);
    }
}
