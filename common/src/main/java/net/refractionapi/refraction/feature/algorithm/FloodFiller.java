package net.refractionapi.refraction.feature.algorithm;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;

import java.util.LinkedList;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.BiPredicate;
import java.util.stream.Stream;

public class FloodFiller {

    protected final Level level;
    protected BlockPos origin;
    protected int maxSpread = 64;
    protected BiPredicate<BlockState, BlockPos> predicate = (state, pos) -> !state.isAir();
    protected BiConsumer<BlockState, BlockPos> postAdd = (state, pos) -> {
    };
    protected Direction[] directions = all();

    protected FloodFiller(Level level, BlockPos origin) {
        this.level = level;
        this.origin = origin;
    }

    public FloodFiller setMaxSpread(int size) {
        this.maxSpread = size;
        return this;
    }

    public FloodFiller testCase(BiPredicate<BlockState, BlockPos> predicate) {
        this.predicate = predicate;
        return this;
    }

    public FloodFiller setDirections(Direction... directions) {
        this.directions = directions;
        return this;
    }

    public FloodFiller postAdd(BiConsumer<BlockState, BlockPos> postAdd) {
        this.postAdd = postAdd;
        return this;
    }

    public FloodFill floodFill(BlockPos origin) {
        this.origin = origin;
        return this.floodFill();
    }

    public FloodFill floodFill() {
        final Stream.Builder<BlockInfo> builder = Stream.builder();
        final LinkedList<BlockPos> queue = new LinkedList<>();
        final List<BlockPos> visited = new LinkedList<>();
        queue.add(this.origin);
        visited.add(this.origin);

        int xMax = this.origin.getX() + this.maxSpread;
        int yMax = this.origin.getY() + this.maxSpread;
        int zMax = this.origin.getZ() + this.maxSpread;
        int xMin = this.origin.getX() - this.maxSpread;
        int yMin = this.origin.getY() - this.maxSpread;
        int zMin = this.origin.getZ() - this.maxSpread;

        while (!queue.isEmpty()) {
            BlockPos pos = queue.poll();
            visited.add(pos);
            BlockState state = this.level.getBlockState(pos);
            int x = pos.getX();
            int y = pos.getY();
            int z = pos.getZ();
            if (x < xMin || x > xMax || y < yMin || y > yMax || z < zMin || z > zMax) {
                continue;
            }
            if (!this.predicate.test(state, pos)) {
                continue;
            }
            builder.accept(new BlockInfo(state, pos));
            this.postAdd.accept(state, pos);
            for (Direction direction : this.directions) {
                BlockPos offset = pos.relative(direction);
                if (queue.contains(offset) || visited.contains(offset)) {
                    continue;
                }
                queue.add(offset);
            }
        }

        return new FloodFill(builder.build().toList());
    }

    public static Direction[] all() {
        return Direction.values();
    }

    public static Direction[] vertical() {
        return new Direction[]{Direction.DOWN, Direction.UP};
    }

    public static Direction[] horizontal() {
        return new Direction[]{Direction.NORTH, Direction.SOUTH, Direction.WEST, Direction.EAST};
    }

    public static FloodFiller create(Level level, BlockPos origin) {
        return new FloodFiller(level, origin);
    }

    public record BlockInfo(BlockState state, BlockPos pos) {

    }

    public record FloodFill(List<BlockInfo> blocks) {

        public AABB createBoundingBox() {
                double minX = Double.MAX_VALUE;
                double minY = Double.MAX_VALUE;
                double minZ = Double.MAX_VALUE;
                double maxX = Double.MIN_VALUE;
                double maxY = -64;
                double maxZ = Double.MIN_VALUE;
                for (BlockInfo block : this.blocks) {
                    BlockPos pos = block.pos;
                    minX = Math.min(minX, pos.getX());
                    minY = Math.min(minY, pos.getY());
                    minZ = Math.min(minZ, pos.getZ());
                    maxX = Math.max(maxX, pos.getX());
                    maxY = Math.max(maxY, pos.getY());
                    maxZ = Math.max(maxZ, pos.getZ());
                }
                return new AABB(minX, minY, minZ, maxX, maxY, maxZ);
            }

            public BlockPos getCenter() {
                return BlockPos.containing(this.createBoundingBox().getCenter());
            }

        }

}
