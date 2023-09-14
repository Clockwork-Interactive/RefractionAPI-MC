package net.refractionapi.refraction.voxels;

import net.minecraft.util.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;

/**
 * Can and does use up a lot of computational time, only designed to run during startup and never mid-game.
 * Always store the values created by this class and never constantly call new objects.
 */
public class VoxelShapeHelper {

    public final VoxelShape northShape;
    public VoxelShape modifiedShape;


    public VoxelShapeHelper(VoxelShape northShape) {
        this.northShape = northShape;
        this.modifiedShape = northShape;
    }

    public VoxelShapeHelper base() {
        modifiedShape = northShape;
        return this;
    }

    /**
     * @return Returns the modified shape (after all the rotation... etc...)
     */
    public VoxelShape getModified() {
        return modifiedShape;
    }

    public VoxelShape[] createYVoxels() {
        VoxelShape[] ret = new VoxelShape[]{modifiedShape, rotateShapeY(modifiedShape, 90), rotateShapeY(modifiedShape, 180), rotateShapeY(modifiedShape, 270)};
        base();
        return ret;
    }

    public VoxelShape[] createXVoxels() {
        VoxelShape[] ret = new VoxelShape[]{modifiedShape, rotateShapeX(modifiedShape, 90), rotateShapeX(modifiedShape, 180), rotateShapeX(modifiedShape, 270)};
        base();
        return ret;
    }

    public VoxelShape[] createZVoxels() {
        VoxelShape[] ret = new VoxelShape[]{modifiedShape, rotateShapeZ(modifiedShape, 270), rotateShapeZ(modifiedShape, 180), rotateShapeZ(modifiedShape, 90)};
        base();
        return ret;
    }

    /**
     * Creates all voxels in the index of the facing values
     * @return An array of voxels based on the 3D data of the facing
     */
    public static VoxelShape[] createFacingVoxels(VoxelShape shape) {
        return new VoxelShape[]{rotateShapeX(shape, 270), rotateShapeX(shape, 90), shape, rotateShapeY(shape, 180), rotateShapeY(shape, 270), rotateShapeY(shape, 90)};
    }

    /*
    All Rotation methods below rotate CCW, annoying but screw redoing this
     */

    public VoxelShapeHelper rotateShapeY(double angle) {
        modifiedShape = rotateShapeY(modifiedShape, angle);
        return this;
    }

    public VoxelShapeHelper rotateShapeX(double angle) {
        modifiedShape = rotateShapeX(modifiedShape, angle);
        return this;
    }

    public VoxelShapeHelper rotateShapeZ(double angle) {
        modifiedShape = rotateShapeZ(modifiedShape, angle);
        return this;
    }

    private static VoxelShape rotateShapeY(VoxelShape shape, double angle) {
        VoxelShape[] buffer = new VoxelShape[]{ shape, VoxelShapes.empty() };
        int times = MathHelper.floor(angle / 90.0D);
        for (int i = 0; i < times; i++) {
            buffer[0].forAllBoxes((minX, minY, minZ, maxX, maxY, maxZ) -> buffer[1] =
                    VoxelShapes.or(buffer[1], VoxelShapes.box(1-maxZ, minY, minX, 1-minZ, maxY, maxX)));
            buffer[0] = buffer[1];
            buffer[1] = VoxelShapes.empty();
        }

        return buffer[0];
    }

    private static VoxelShape rotateShapeX(VoxelShape shape, double angle) {
        VoxelShape[] buffer = new VoxelShape[]{ shape, VoxelShapes.empty() };
        int times = MathHelper.floor(angle / 90.0D);
        for (int i = 0; i < times; i++) {
            buffer[0].forAllBoxes((minX, minY, minZ, maxX, maxY, maxZ) -> buffer[1] =
                    VoxelShapes.or(buffer[1], VoxelShapes.box(minX, minZ, 1-maxY, maxX, maxZ, 1-minY)));
            buffer[0] = buffer[1];
            buffer[1] = VoxelShapes.empty();
        }

        return buffer[0];
    }

    private static VoxelShape rotateShapeZ(VoxelShape shape, double angle) {
        VoxelShape[] buffer = new VoxelShape[]{ shape, VoxelShapes.empty() };
        int times = MathHelper.floor(angle / 90.0D);
        for (int i = 0; i < times; i++) {
            buffer[0].forAllBoxes((minX, minY, minZ, maxX, maxY, maxZ) -> buffer[1] =
                    VoxelShapes.or(buffer[1], VoxelShapes.box(minY, 1-maxX, minZ, maxY, 1-minX, maxZ)));
            buffer[0] = buffer[1];
            buffer[1] = VoxelShapes.empty();
        }

        return buffer[0];
    }

    /**
     * @return Returns Index value counterclockwise from North in Top-Down Direction (NWSE | 0123)
     */
    public static int getYIndex(Direction direction) {
        return direction.getOpposite().get2DDataValue();
    }

    /**
     * @return Returns Index value counterclockwise from North/West in X (NDSU/WDEU | 0123)
     */
    public static int getXZIndex(Direction direction) {
        switch (direction) {
            default:
            case NORTH:
            case WEST: return 0;
            case DOWN: return 1;
            case SOUTH:
            case EAST: return 2;
            case UP: return 3;
        }
    }
}
