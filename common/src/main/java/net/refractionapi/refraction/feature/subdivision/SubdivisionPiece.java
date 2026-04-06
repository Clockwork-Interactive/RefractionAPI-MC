package net.refractionapi.refraction.feature.subdivision;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;
import net.refractionapi.refraction.helper.randomizer.WeightedRandom;
import net.refractionapi.refraction.helper.vec3.Vec3Helper;

import javax.annotation.Nullable;
import java.util.*;
import java.util.function.Consumer;

public class SubdivisionPiece {
    private final ResourceLocation location;
    protected final Subdivision.StructCache struct;
    protected final Vec3i size;
    protected final HashMap<Door, TakenDoor> takenDoors = new HashMap<>();
    BlockPos pivot;
    final ServerLevel serverLevel;
    final SubdivisionPiece connecting;
    BlockPos spawn;
    int rotationSteps = 0;

    public SubdivisionPiece(
            ResourceLocation location,
            ServerLevel serverLevel,
            SubdivisionPiece connecting,
            BlockPos spawn
    ) {
        this.location = location;
        this.serverLevel = serverLevel;
        this.connecting = connecting;
        this.spawn = spawn;
        this.struct = getStruct();
        this.size = struct.template().getSize();
        pivot = relativeCenter();
    }

    public ResourceLocation id() {
        return location;
    }

    public void tick() {
        // ticks when a player is in
        // the bounding box of the struct --Zeus
    }

    public Rotation rotation() {
        return Rotation.values()[rotationSteps % 4];
    }

    public @Nullable TakenDoor getTakenDoor(Door door) {
        return takenDoors.get(door);
    }

    public @Nullable TakenDoor getTakenDoor(BlockPos pos) {
        return takenDoors.values().stream().filter(takenDoor -> takenDoor.isApartOfDoor(pos)).findFirst().orElse(null);
    }

    public List<Door> availableDoors() {
        return struct.doors().stream().filter(door -> !isDoorTaken(door)).toList();
    }

    public int maxDoorCount() {
        return struct.doors().size();
    }

    public void occupyDoor(Door door) {
        takenDoors.put(door, new TakenDoor(this, door));
    }

    public boolean isDoorTaken(Door door) {
        return takenDoors.containsKey(door);
    }

    public BlockPos relativeCenter() {
        return BlockPos.containing(
                (double) size.getX() / 2,
                (double) size.getY() / 2,
                (double) size.getZ() / 2
        );
    }

    public BlockPos spawn() {
        return spawn;
    }

    private Subdivision.StructCache getStruct() {
        return Subdivision.getInstance().getPiece(id());
    }

    public static class TakenDoor {
        final SubdivisionPiece piece;
        final Door door;
        boolean failedToGenerate = false;

        public TakenDoor(SubdivisionPiece piece, Door door) {
            this.piece = piece;
            this.door = door;
        }

        public boolean isApartOfDoor(BlockPos pos) {
            return door.positions().stream().anyMatch(doorPos -> doorPos.equals(pos));
        }

        public Door door() {
            return door;
        }

        public boolean failed() {
            return failedToGenerate;
        }

        public void markFailed() {
            this.failedToGenerate = true;
        }
    }

    public record Door(
            int[] size,
            BlockPos doorCenter,
            Direction direction,
            Set<BlockPos> positions
    ) {
    }

    public static class DoorBuilder {
        public Set<BlockPos> positions = new HashSet<>();
        public int[] size;
        public BlockPos doorCenter;
        public Direction direction;

        public Door create(Vec3i structSize) {
            if (positions.isEmpty()) return null;
            var min = Vec3Helper.getMin(positions);
            var max = Vec3Helper.getMax(positions);
            int xSize = max.getX() - min.getX() + 1;
            int ySize = max.getY() - min.getY() + 1;
            int zSize = max.getZ() - min.getZ() + 1;
            int longestWidth = Math.max(xSize, zSize);
            size = new int[]{longestWidth, ySize};
            doorCenter = BlockPos.containing(
                    (double) (min.getX() + max.getX()) / 2,
                    (double) (min.getY() + max.getY()) / 2,
                    (double) (min.getZ() + max.getZ()) / 2
            );
            direction = Subdivision.getDirection(structSize, doorCenter);
            return new Door(size, doorCenter, direction, positions);
        }
    }

    public static class Configurer {
        protected final HashMap<Integer, WeightedRandom<BlockState>> randomBlockSet = new HashMap<>();
        protected final HashMap<Integer, WeightedRandom<Configurer>> randomStructSet = new HashMap<>();
        protected final ResourceLocation id;
        protected Factory factory = SubdivisionPiece::new;
        protected float weight = 1.0F;
        protected int budget = 0;

        public Configurer(ResourceLocation id) {
            this.id = id;
        }

        public BlockState pullRandomBlock(int id) {
            return randomBlockSet.containsKey(id) ? randomBlockSet.get(id).get() : null;
        }

        public Configurer setRandomBlocks(int id, Consumer<WeightedRandom<BlockState>> consumer) {
            WeightedRandom<BlockState> weightedRandom = new WeightedRandom<>();
            consumer.accept(weightedRandom);
            return setRandomBlocks(id, weightedRandom);
        }

        public Configurer setRandomBlocks(int id, WeightedRandom<BlockState> random) {
            randomBlockSet.put(id, random);
            return this;
        }

        public Configurer setRandomStruct(int id, Consumer<WeightedRandom<Configurer>> consumer) {
            WeightedRandom<Configurer> weightedRandom = new WeightedRandom<>();
            consumer.accept(weightedRandom);
            randomStructSet.put(id, weightedRandom);
            return this;
        }

        public Configurer setFactory(Factory factory) {
            this.factory = factory;
            return this;
        }

        public Configurer setWeight(float weight) {
            this.weight = weight;
            return this;
        }

        public Configurer setBudget(int budget) {
            this.budget = budget;
            return this;
        }
    }

    @FunctionalInterface
    public interface Factory {
        SubdivisionPiece create(
                ResourceLocation id,
                ServerLevel serverLevel,
                SubdivisionPiece connecting,
                BlockPos spawn
        );
    }
}

