package net.refractionapi.refraction.feature.subdivision;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.refractionapi.refraction.Refraction;
import net.refractionapi.refraction.helper.randomizer.WeightedRandom;
import net.refractionapi.refraction.helper.vec3.Vec3Helper;

import java.util.HashMap;
import java.util.HashSet;
import java.util.function.Consumer;

public class SubdivisionPiece {
    private final ResourceLocation location;
    protected final Subdivision.StructCache struct;
    protected final Vec3i size;
    protected final HashSet<Door> takenDoors = new HashSet<>();
    final ServerLevel serverLevel;
    final SubdivisionPiece connecting;
    final BlockPos spawn;
    protected BlockPos spawnAbsolute;
    final BlockPos offset;
    int rotationSteps = 0;

    public SubdivisionPiece(
            ResourceLocation location,
            ServerLevel serverLevel,
            SubdivisionPiece connecting,
            BlockPos spawn,
            BlockPos offset
    ) {
        this.location = location;
        this.serverLevel = serverLevel;
        this.connecting = connecting;
        this.offset = offset;
        this.spawn = spawn;
        this.struct = getStruct();
        this.size = struct.template().getSize();
    }

    public ResourceLocation id() {
        return location;
    }

    public void tick() {
        // ticks when a player is in
        // the bounding box of the struct --Zeus
    }

    public void occupyDoor(Door door) {
        takenDoors.add(door);
    }

    public boolean isDoorTaken(Door door) {
        return takenDoors.contains(door);
    }

    public void place(Door door) {
        if (door != null) occupyDoor(door);
        Refraction.LOGGER.info("{} {} {}", spawn.offset(offset), rotationSteps, id());

        Rotation rotation = switch (rotationSteps % 4) {
            case 1 -> Rotation.CLOCKWISE_90;
            case 2 -> Rotation.CLOCKWISE_180;
            case 3, -1 -> Rotation.COUNTERCLOCKWISE_90;
            default -> Rotation.NONE;
        };

        BlockPos rotationPivot = door == null ? BlockPos.ZERO : door.doorCenter().below();

        struct.template().placeInWorld(
                serverLevel,
                spawnAbsolute = spawn.offset(rotationPivot),
                BlockPos.ZERO,
                new StructurePlaceSettings().setRotation(rotation),
                serverLevel.random,
                18
        );
        struct.doors().forEach((dr) -> {
            BlockPos doorCenter = dr.doorCenter();
            BlockPos rotatedDoor = dr.doorCenter().rotate(rotation);
            serverLevel.setBlock(spawnAbsolute.offset(rotatedDoor), Blocks.DIAMOND_BLOCK.defaultBlockState(), 2);
            serverLevel.setBlock(spawnAbsolute.offset(doorCenter), Blocks.DIAMOND_BLOCK.defaultBlockState(), 2);
        });
        serverLevel.setBlock(spawnAbsolute, Blocks.IRON_BLOCK.defaultBlockState(), 2);
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
        return Subdivision.getInstance().get(id());
    }

    public record Door(
            int[] size,
            BlockPos doorCenter,
            Direction direction
    ) {
    }

    public static class Configurer {
        protected final HashMap<Integer, WeightedRandom<BlockState>> randomBlockSet = new HashMap<>();
        protected final HashMap<Integer, WeightedRandom<Configurer>> randomStructSet = new HashMap<>();
        protected final ResourceLocation id;
        protected Factory factory = SubdivisionPiece::new;
        protected float weight = 1.0F;

        public Configurer(ResourceLocation id) {
            this.id = id;
        }

        public Configurer setRandomBlocks(int id, Consumer<WeightedRandom<BlockState>> consumer) {
            WeightedRandom<BlockState> weightedRandom = new WeightedRandom<>();
            consumer.accept(weightedRandom);
            randomBlockSet.put(id, weightedRandom);
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
    }

    @FunctionalInterface
    public interface Factory {
        SubdivisionPiece create(
                ResourceLocation id,
                ServerLevel serverLevel,
                SubdivisionPiece connecting,
                BlockPos spawn,
                BlockPos offset
        );
    }
}
