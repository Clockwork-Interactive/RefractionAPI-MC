package net.refractionapi.refraction.feature.subdivision;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.refractionapi.refraction.Refraction;
import net.refractionapi.refraction.helper.randomizer.WeightedRandom;

import java.util.Arrays;
import java.util.Set;

public class SubdivisionGenerator {
    final MinecraftServer server;

    public SubdivisionGenerator(MinecraftServer server) {
        this.server = server;
    }

    public void generate(SubdivisionSet set, ServerLevel serverLevel, BlockPos center) {
        SubdivisionPiece.Configurer origin = set.origin();
        if (origin == null) return;
        SubdivisionPiece originPiece = place(
                serverLevel,
                center,
                origin,
                0
        );
        for (SubdivisionPiece.Door door : originPiece.struct.doors()) {
            Refraction.LOGGER.info(String.valueOf(door.direction()));
        }
        if (originPiece == null) return;
        WeightedRandom<SubdivisionPiece.Configurer> toPlace = set.pieces();
        int maxDepth = set.maxDepth;
        for (int i = 0; i < maxDepth; i++) {
            SubdivisionPiece lastPiece = originPiece;
            int attempts = (lastPiece.struct.doors().size() - lastPiece.takenDoors.size()) * 2;
            for (int j = 0; j < attempts; j++) {
                SubdivisionPiece.Configurer nextConfig = toPlace.get();
                SubdivisionPiece newPiece = place(serverLevel, lastPiece, nextConfig);
                lastPiece = newPiece == null ? lastPiece : newPiece;
            }
        }
    }

    protected SubdivisionPiece place(ServerLevel serverLevel, BlockPos center, SubdivisionPiece.Configurer piece, int rotation) {
        Subdivision.StructCache struct = get(piece.id);
        if (struct == null) return null;
        Vec3i size = struct.template().getSize();
        BlockPos spawn = center.offset(-size.getX() / 2, 0, -size.getZ() / 2);
        return addPiece(piece.factory.create(piece.id, serverLevel, null, spawn, BlockPos.ZERO), null, rotation);
    }

    private SubdivisionPiece place(ServerLevel serverLevel, SubdivisionPiece previous, SubdivisionPiece.Configurer piece) {
        Subdivision.StructCache newStruct = get(piece.id);
        Subdivision.StructCache oldStruct = previous.struct;
        if (newStruct == null) return null;
        Vec3i previousSize = previous.size;
        Vec3i newSize = newStruct.template().getSize();
        Set<SubdivisionPiece.Door> oldDoors = oldStruct.doors();
        Set<SubdivisionPiece.Door> newDoors = newStruct.doors();
        SubdivisionPiece.Door matchOld = null;
        SubdivisionPiece.Door matchNew = null;
        for (SubdivisionPiece.Door oldDoor : oldDoors)
            for (SubdivisionPiece.Door newDoor : newDoors) {
                if (!previous.isDoorTaken(oldDoor) && Arrays.equals(oldDoor.size(), newDoor.size())) {
                    matchOld = oldDoor;
                    matchNew = newDoor;
                    previous.occupyDoor(oldDoor);
                    break;
                }
            }
        if (matchOld == null) return null;
        // based on the rotation needed to align the doors,
        // calculate the new spawn position and rotation --Zeus
        Direction oldDir = matchOld.direction().getOpposite();
        Direction newDir = matchNew.direction();
        int previousRotation = previous.rotationSteps;
        //int rotationSteps = (newDir.get2DDataValue() - oldDir.get2DDataValue() + 4) % 4;
        int rotationSteps = (newDir.get2DDataValue() - oldDir.get2DDataValue() + previousRotation) % 4;
        Direction finalDir = Direction.from2DDataValue(rotationSteps);
        BlockPos doorCenterPrevious = matchOld.doorCenter();
        BlockPos doorCenterNew = matchNew.doorCenter();
        Refraction.LOGGER.info("{} {} {}", oldDir, newDir, finalDir);
        return addPiece(piece.factory.create(
                piece.id,
                serverLevel,
                previous,
                previous.spawnAbsolute.offset(10,0,10),
                BlockPos.ZERO
        ), matchNew, rotationSteps);
    }

    private SubdivisionPiece addPiece(SubdivisionPiece piece, SubdivisionPiece.Door door, int rotationSteps) {
        piece.rotationSteps = rotationSteps;
        piece.place(door);
        return piece;
    }

    private Subdivision.StructCache get(ResourceLocation id) {
        return Subdivision.getInstance().get(id);
    }

    private Direction dir(BlockPos from, BlockPos to) {
        BlockPos diff = to.subtract(from);
        if (Math.abs(diff.getX()) > Math.abs(diff.getZ())) {
            return diff.getX() > 0 ? Direction.EAST : Direction.WEST;
        } else {
            return diff.getZ() > 0 ? Direction.SOUTH : Direction.NORTH;
        }
    }
}
