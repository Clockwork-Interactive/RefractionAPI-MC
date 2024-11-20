package net.refractionapi.refraction.client.screen;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.refractionapi.refraction.Refraction;
import net.refractionapi.refraction.feature.algorithm.MazeGenerator;
import net.refractionapi.refraction.feature.examples.screen.ExampleScreenRegistry;
import net.refractionapi.refraction.feature.screen.ServerScreen;

public class RDashboardServer extends ServerScreen {

    public RDashboardServer(ServerPlayer player) {
        super(ExampleScreenRegistry.DASHBOARD, player);
    }

    @Override
    public boolean stillValid() {
        return Refraction.debugTools;
    }

    @Override
    public void handle(CompoundTag tag) {
        if (tag.getString("type").equals("maze")) {
            createMaze(tag);
        }
    }

    private void createMaze(CompoundTag tag) {
        int size = tag.getInt("size");
        int wallHeight = tag.getInt("wallHeight");
        int wallThickness = tag.getInt("wallThickness");
        int centerSize = tag.getInt("centerSize");
        int[] start = tag.getIntArray("start");
        BlockPos startBlock = new BlockPos(start[0], start[1], start[2]);
        Direction startDirection = Direction.byName(tag.getString("startDirection").toUpperCase());
        Block wallBlock = getBlock(tag.getString("wallBlock"));
        Block wallOuterBlock = getBlock(tag.getString("wallOuterBlock"));
        Block floorBlock = getBlock(tag.getString("floorBlock"));
        Block ceilingBlock = getBlock(tag.getString("ceilingBlock"));
        MazeGenerator generator = new MazeGenerator(getPlayer().serverLevel());
        generator.setCeilingBlock(ceilingBlock.defaultBlockState());
        generator.setFloorBlock(floorBlock.defaultBlockState());
        generator.setWallBlock(wallBlock.defaultBlockState());
        generator.setWallOuterBlock(wallOuterBlock.defaultBlockState());
        generator.setWallHeight(wallHeight);
        generator.setWallThickness(wallThickness);
        generator.setCenterSize(centerSize);
        generator.setMazeSize(size);
        generator.setStart(startBlock, startDirection);
        generator.generate();
    }

    private Block getBlock(String name) {
        Block block = BuiltInRegistries.BLOCK.get(ResourceLocation.tryParse(name));
        return block == null ? Blocks.AIR : block;
    }

}
