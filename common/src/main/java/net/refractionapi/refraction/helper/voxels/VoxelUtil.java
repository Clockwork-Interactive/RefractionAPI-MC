package net.refractionapi.refraction.helper.voxels;

import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.refractionapi.refraction.helper.registry.block.BaseHorizontalBlock;

public class VoxelUtil {

    public static VoxelShape[] makeHorizontalShapes(VoxelShape northShape) {
        return new VoxelShape[]{rotateShape(Direction.SOUTH, northShape), rotateShape(Direction.WEST, northShape), northShape, rotateShape(Direction.EAST, northShape)};
    }

    public static VoxelShape[] makeRotatedXShape(VoxelShape northShape) {
        return new VoxelShape[]{northShape, rotateShapeX(northShape, 90), rotateShapeX(northShape, 180), rotateShapeX(northShape, 270)};
    }

    public static VoxelShape[] makeRotatedZShape(VoxelShape northShape) {
        return new VoxelShape[]{northShape, rotateShapeZ(northShape, 270), rotateShapeZ(northShape, 180), rotateShapeZ(northShape, 90)};
    }

    private static VoxelShape rotateShape(Direction to, VoxelShape shape) {
        VoxelShape[] buffer = new VoxelShape[]{ shape, Shapes.empty() };
        int times = (to.get2DDataValue() - Direction.NORTH.get2DDataValue() + 4) % 4;
        for (int i = 0; i < times; i++) {
            buffer[0].forAllBoxes((minX, minY, minZ, maxX, maxY, maxZ) -> buffer[1] =
                    Shapes.or(buffer[1], Shapes.box(1-maxZ, minY, minX, 1-minZ, maxY, maxX)));
            buffer[0] = buffer[1];
            buffer[1] = Shapes.empty();
        }

        return buffer[0];
    }

    public static VoxelShape rotateShapeY(VoxelShape shape, double angle) {
        VoxelShape[] buffer = new VoxelShape[]{ shape, Shapes.empty() };
        int times = Mth.floor(angle / 90.0D);
        for (int i = 0; i < times; i++) {
            buffer[0].forAllBoxes((minX, minY, minZ, maxX, maxY, maxZ) -> buffer[1] =
                    Shapes.or(buffer[1], Shapes.box(1-maxZ, minY, minX, 1-minZ, maxY, maxX)));
            buffer[0] = buffer[1];
            buffer[1] = Shapes.empty();
        }

        return buffer[0];
    }

    public static VoxelShape rotateShapeX(VoxelShape shape, double angle) {
        VoxelShape[] buffer = new VoxelShape[]{ shape, Shapes.empty() };
        int times = Mth.floor(angle / 90.0D);
        for (int i = 0; i < times; i++) {
            buffer[0].forAllBoxes((minX, minY, minZ, maxX, maxY, maxZ) -> buffer[1] =
                    Shapes.or(buffer[1], Shapes.box(minX, minZ, 1-maxY, maxX, maxZ, 1-minY)));
            buffer[0] = buffer[1];
            buffer[1] = Shapes.empty();
        }

        return buffer[0];
    }

    public static VoxelShape rotateShapeZ(VoxelShape shape, double angle) {
        VoxelShape[] buffer = new VoxelShape[]{ shape, Shapes.empty() };
        int times = Mth.floor(angle / 90.0D);
        for (int i = 0; i < times; i++) {
            buffer[0].forAllBoxes((minX, minY, minZ, maxX, maxY, maxZ) -> buffer[1] =
                    Shapes.or(buffer[1], Shapes.box(minY, 1-maxX, minZ, maxY, 1-minX, maxZ)));
            buffer[0] = buffer[1];
            buffer[1] = Shapes.empty();
        }

        return buffer[0];
    }

    public static int getDirection(BlockState state) {
        return state.getValue(BaseHorizontalBlock.FACING).get2DDataValue();
    }

}


