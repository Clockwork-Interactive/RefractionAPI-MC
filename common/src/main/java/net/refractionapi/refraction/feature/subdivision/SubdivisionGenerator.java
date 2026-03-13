package net.refractionapi.refraction.feature.subdivision;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.phys.AABB;
import net.refractionapi.refraction.debug.RDebugRenderers;
import net.refractionapi.refraction.feature.subdivision.processors.BlockRandomizerProcessor;
import net.refractionapi.refraction.feature.subdivision.processors.DoorReplacementProcessor;
import net.refractionapi.refraction.helper.randomizer.WeightedRandom;
import net.refractionapi.refraction.util.Tuple;

import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.Queue;

public class SubdivisionGenerator {
    final MinecraftServer server;

    public SubdivisionGenerator(MinecraftServer server) {
        this.server = server;
    }

    public void generate(SubdivisionSet set, ServerLevel serverLevel, BlockPos center) {
        var generatedSet = new GeneratedSet();
        var origin = set.origin();
        if (origin == null) return;
        var originPiece = generateSingle(
                serverLevel,
                center,
                set,
                origin,
                0
        );
        if (originPiece == null) return;
        generatedSet.addPiece(originPiece);
        var toPlace = set.pieces();
        int maxDepth = 32; // hard cap for now, will be changeable through code later --Zeus
        var branchQueue = new LinkedList<SubdivisionPiece>();
        branchQueue.add(originPiece);
        // (might need optimizations for big dungeons)
        // branching algo
        // might redo idk --Zeus
        processBranches(generatedSet, set, serverLevel, toPlace, branchQueue, maxDepth);
    }
    
    private void processBranches(
            GeneratedSet generatedSet,
            SubdivisionSet set,
            ServerLevel serverLevel,
            WeightedRandom<SubdivisionPiece.Configurer> toPlace,
            Queue<SubdivisionPiece> branchQueue,
            int maxDepth
    ) {
        int depth = 0;
        while (!branchQueue.isEmpty() && depth < maxDepth) {
            var currentPiece = branchQueue.poll();
            if (currentPiece == null) continue;
            tryGenerateBranches(generatedSet, set, serverLevel, toPlace, currentPiece, branchQueue);
            depth++;
        }
    }
    
    private void tryGenerateBranches(
            GeneratedSet generatedSet,
            SubdivisionSet set,
            ServerLevel serverLevel,
            WeightedRandom<SubdivisionPiece.Configurer> toPlace,
            SubdivisionPiece currentPiece,
            Queue<SubdivisionPiece> branchQueue
    ) {
        if (maxDoorsReached(currentPiece)) return;
        int branchAttempts = currentPiece.maxDoorCount();
        for (int i = 0; i < branchAttempts; i++) {
            if (maxDoorsReached(currentPiece)) break;
            var nextPieceConfig = toPlace.get();
            if (nextPieceConfig == null) continue;
            var newPiece = place(generatedSet, set, serverLevel, currentPiece, nextPieceConfig);
            if (newPiece != null) branchQueue.add(newPiece);
        }
    }
    
    private boolean maxDoorsReached(SubdivisionPiece piece) {
        return piece.takenDoors.size() >= piece.maxDoorCount();
    }

    protected SubdivisionPiece generateSingle(ServerLevel serverLevel, BlockPos center, @Nullable SubdivisionSet set, SubdivisionPiece.Configurer piece, int rotationSteps) {
        var struct = cache(piece.id);
        if (struct == null) return null;
        var size = struct.template().getSize();
        var spawn = center.offset(-size.getX() / 2, 0, -size.getZ() / 2);
        return place(set, piece.factory.create(piece.id, serverLevel, null, spawn), null, rotationSteps);
    }

    private SubdivisionPiece place(GeneratedSet generatedSet, SubdivisionSet set, ServerLevel serverLevel, SubdivisionPiece previous, SubdivisionPiece.Configurer piece) {
        var newStruct = cache(piece.id);
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
        var newSpawn = prevSpawn.offset(blockOffset);
        if (isColliding(generatedSet, newSpawn, newStructSize)) return null;
        var newPiece = place(set, piece.factory.create(
                piece.id,
                serverLevel,
                previous,
                prevSpawn.offset(blockOffset)
        ), currDoor, rotationSteps);
        generatedSet.addPiece(newPiece);
        return newPiece;
    }

    public boolean isColliding(GeneratedSet set, BlockPos spawn, Vec3i size) {
        return set.isColliding(spawn, size);
    }

    private SubdivisionPiece place(SubdivisionPiece piece, SubdivisionPiece.Door door, int rotationSteps) {
        return place(null, piece, door, rotationSteps);
    }

    private SubdivisionPiece place(SubdivisionSet set, SubdivisionPiece piece, SubdivisionPiece.Door door, int rotationSteps) {
        piece.rotationSteps = rotationSteps;
        if (door != null) piece.occupyDoor(door);
        var rotation = Rotation.values()[rotationSteps % 4];
        // TODO fallback w place check and cache --Zeus
        piece.struct.template().placeInWorld(
                piece.serverLevel,
                piece.spawn,
                BlockPos.ZERO,
                new StructurePlaceSettings()
                        .addProcessor(new BlockRandomizerProcessor(set, piece, door))
                        .addProcessor(new DoorReplacementProcessor(set, piece, door))
                        .setRotationPivot(piece.relativeCenter())
                        .setRotation(rotation),
                piece.serverLevel.random,
                18
        );
        return piece;
    }

    private Subdivision.StructCache cache(ResourceLocation id) {
        return Subdivision.getInstance().getPiece(id);
    }

    private SubdivisionSet set(ResourceLocation id) {
        return SubdivisionRegistry.get(id);
    }
}
