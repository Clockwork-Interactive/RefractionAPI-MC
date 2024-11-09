package net.refractionapi.refraction.helper.vec3;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import java.util.function.BiConsumer;

/**
 * Rotation (Refraction) Axis Aligned Box <br>
 * This hitbox system is non-world-grid aligned, and can be diagonal.
 */
// Zeus' personal note: https://tenor.com/view/breaking-bad-jesse-pinkman-sad-jesse-pinkman-sad-breaking-bad-jesse-pinkman-sad-gif-24555445
public class RAAB extends AABB {

    public final Vec3[][] positions;

    private RAAB(Vec3[][] positions, double x1, double y1, double z1, double x2, double y2, double z2) {
        super(x1, y1, z1, x2, y2, z2);
        this.positions = positions;
    }

    public static RAAB create(Vec3 bottomLeft1, Vec3 bottomRight1, Vec3 topRight1, Vec3 topLeft1, Vec3 bottomLeft2, Vec3 bottomRight2, Vec3 topRight2, Vec3 topLeft2) {
        return create(
                bottomLeft1.x, bottomLeft1.y, bottomLeft1.z,
                bottomRight1.x, bottomRight1.y, bottomRight1.z,
                topRight1.x, topRight1.y, topRight1.z,
                topLeft1.x, topLeft1.y, topLeft1.z,
                bottomLeft2.x, bottomLeft2.y, bottomLeft2.z,
                bottomRight2.x, bottomRight2.y, bottomRight2.z,
                topRight2.x, topRight2.y, topRight2.z,
                topLeft2.x, topLeft2.y, topLeft2.z
        );
    }

    public static RAAB create(BlockPos bottomLeft, BlockPos bottomRight, BlockPos topRight, BlockPos topLeft) {
        return create(
                bottomLeft.getX(), bottomLeft.getY(), bottomLeft.getZ(),
                bottomRight.getX(), bottomRight.getY(), bottomRight.getZ(),
                topRight.getX(), topRight.getY(), topRight.getZ(),
                topLeft.getX(), topLeft.getY(), topLeft.getZ()
        );
    }

    public static RAAB create(BlockPos bottomLeft, BlockPos topRight) {
        BlockPos[] corners = Vec3Helper.createRectangle(bottomLeft, topRight);
        return create(bottomLeft, corners[0], topRight, corners[1]);
    }

    public static RAAB create(Vec3 bottomLeft, Vec3 topRight) {
        Vec3[] corners = Vec3Helper.createRectangle(bottomLeft, topRight);
        return create(bottomLeft, corners[0], topRight, corners[1]);
    }

    public static RAAB create(Vec3 bottomLeft, Vec3 bottomRight, Vec3 topRight, Vec3 topLeft) {
        return create(
                bottomLeft.x, bottomLeft.y, bottomLeft.z,
                bottomRight.x, bottomRight.y, bottomRight.z,
                topRight.x, topRight.y, topRight.z,
                topLeft.x, topLeft.y, topLeft.z
        );
    }

    public static RAAB create(double... coords) {
        if (!(coords.length == 12 || coords.length == 24))
            throw new InstantiationError("Coord length is not supported (12 or 24 only)");
        double[] finalCoods = new double[24];
        System.arraycopy(coords, 0, finalCoods, 0, coords.length);
        if (coords.length == 12) {
            System.arraycopy(coords, 0, finalCoods, 12, coords.length);
        }
        Vec3[][] positions = new Vec3[2][4];
        for (int i = 0; i < finalCoods.length; i += 3) {
            positions[i / 12][i % 4] = new Vec3(finalCoods[i], finalCoods[i + 1], finalCoods[i + 2]);
        }
        transform(positions);
        double[] minMax = getMinMax(positions);
        double minX = minMax[0];
        double minY = minMax[1];
        double minZ = minMax[2];
        double maxX = minMax[3];
        double maxY = minMax[4];
        double maxZ = minMax[5];
        if (!validateBox(positions))
            throw new InstantiationError("Invalid box, coordinates either don't line up or are null");
        return new RAAB(positions, minX, minY, minZ, maxX, maxY, maxZ);
    }

    public void offsetBottom(float y) {
        for (int i = 0; i < this.positions[0].length; i++) {
            this.positions[0][i] = this.positions[0][i].add(0, y, 0);
        }
    }

    /**
     * Using the <a href="https://research.ncl.ac.uk/game/mastersdegree/gametechnologies/previousinformation/physics4collisiondetection/2017%20Tutorial%204%20-%20Collision%20Detection.pdf">Separating Axis Theorem</a> <br>
     * to check if the box intersects with another box <br>
     * There's probably a faster algorithm, pull request if you find one :P --Zeus
     */
    @Override
    public boolean intersects(double x1, double y1, double z1, double x2, double y2, double z2) {
        Vec3 start = new Vec3(x1, y1, z1);
        Vec3 end = new Vec3(x2, y2, z2);
        Vec3 direction = end.subtract(start);
        Vec3[] axes = getAxes();

        for (Vec3 axis : axes) {
            if (!overlapOnAxis(axis, start, direction)) {
                return false;
            }
        }
        return true;
    }

    @Override
    public boolean contains(double x, double y, double z) {
        Vec3 point = transformToLocal(new Vec3(x, y, z));
        double u = point.x;
        double v = point.y;
        double w = point.z;
        return (u >= 0 && u <= 1) && (v >= 0 && v <= 1) && (w >= 0 && w <= 1);
    }

    /**
     * Converts vec3 to local coordinates using the <br>
     * <a href="https://en.wikipedia.org/wiki/Barycentric_coordinate_system">Barycentric Coordinate system</a>"
     */
    private Vec3 transformToLocal(Vec3 point) {
        Vec3 bottomLeft = positions[0][0];
        Vec3 bottomRightLocal = positions[0][1].subtract(bottomLeft);
        Vec3 topLeftLocal = positions[0][3].subtract(bottomLeft);
        Vec3 bottomLeftLocal = positions[1][0].subtract(bottomLeft);

        Vec3 pointLocal = point.subtract(bottomLeft);
        double brSqrLength = bottomRightLocal.dot(bottomRightLocal);
        double brTlAlign = bottomRightLocal.dot(topLeftLocal);
        double brBtAlign = bottomRightLocal.dot(bottomLeftLocal);
        double tlSqrLength = topLeftLocal.dot(topLeftLocal);
        double tLBlAlign = topLeftLocal.dot(bottomLeftLocal);
        double bLSqrLength = bottomLeftLocal.dot(bottomLeftLocal);
        double pLBrAlign = pointLocal.dot(bottomRightLocal);
        double pLTlAlign = pointLocal.dot(topLeftLocal);
        double pLBlAlign = pointLocal.dot(bottomLeftLocal);

        double determinant = brSqrLength * (tlSqrLength * bLSqrLength - tLBlAlign * tLBlAlign) -
                brTlAlign * (brTlAlign * bLSqrLength - tLBlAlign * brBtAlign) +
                brBtAlign * (brTlAlign * tLBlAlign - tlSqrLength * brBtAlign);

        double invDet = 1.0 / determinant;
        // barycentric coordinates :D
        double u = (tlSqrLength * bLSqrLength - tLBlAlign * tLBlAlign) * pLBrAlign +
                (brBtAlign * tLBlAlign - brTlAlign * bLSqrLength) * pLTlAlign +
                (brTlAlign * tLBlAlign - brBtAlign * tlSqrLength) * pLBlAlign;
        u *= invDet;

        double v = (tLBlAlign * brBtAlign - bLSqrLength * brTlAlign) * pLBrAlign +
                (brSqrLength * bLSqrLength - brBtAlign * brBtAlign) * pLTlAlign +
                (brBtAlign * brTlAlign - brSqrLength * tLBlAlign) * pLBlAlign;
        v *= invDet;

        double w = (brTlAlign * tLBlAlign - tlSqrLength * brBtAlign) * pLBrAlign +
                (brBtAlign * brTlAlign - brSqrLength * tLBlAlign) * pLTlAlign +
                (brSqrLength * tlSqrLength - brTlAlign * brTlAlign) * pLBlAlign;
        w *= invDet;

        return new Vec3(u, v, w);
    }

    private Vec3[] getAxes() {
        Vec3[] axes = new Vec3[20];
        int index = 0;

        for (int i = 0; i < 2; i++) {
            for (int j = 0; j < 4; j++) {
                Vec3 edge = this.positions[i][(j + 1) % 4].subtract(this.positions[i][j]);
                axes[index++] = edge.normalize();
            }
        }

        for (int i = 0; i < 4; i++) {
            Vec3 edge = this.positions[0][(i + 1) % 4].subtract(this.positions[0][i]);
            axes[index++] = edge.cross(new Vec3(1, 0, 0)).normalize();
            axes[index++] = edge.cross(new Vec3(0, 1, 0)).normalize();
            axes[index++] = edge.cross(new Vec3(0, 0, 1)).normalize();
        }

        return axes;
    }

    private boolean overlapOnAxis(Vec3 axis, Vec3 start, Vec3 direction) {
        double[] prismProjection = projectPrism(axis);
        double[] lineProjection = projectLine(axis, start, direction);

        return prismProjection[1] >= lineProjection[0] && lineProjection[1] >= prismProjection[0];
    }

    private double[] projectPrism(Vec3 axis) {
        double min = Double.MAX_VALUE;
        double max = Double.MIN_VALUE;

        for (Vec3[] layer : this.positions) {
            for (Vec3 vertex : layer) {
                double projection = vertex.dot(axis);
                min = Math.min(min, projection);
                max = Math.max(max, projection);
            }
        }

        return new double[]{min, max};
    }

    private double[] projectLine(Vec3 axis, Vec3 start, Vec3 direction) {
        double startProjection = start.dot(axis);
        double endProjection = start.add(direction).dot(axis);

        return new double[]{Math.min(startProjection, endProjection), Math.max(startProjection, endProjection)};
    }

    public AABB getBox() {
        return new AABB(this.minX, this.minY, this.minZ, this.maxX, this.maxY, this.maxZ);
    }

    public void forCorners(BiConsumer<Integer[], Vec3> consumer) {
        for (int i = 0; i < this.positions.length; i++) {
            for (int j = 0; j < this.positions[i].length; j++) {
                consumer.accept(new Integer[]{i, j}, this.positions[i][j]);
            }
        }
    }

    // Am I crazy?? -Zeus
    private static void transform(Vec3[][] positions) {
        final Direction eastRelative = Vec3Helper.getDirection(positions[0][0], positions[0][1]);
        final Direction northRelative = Vec3Helper.getDirection(positions[0][0], positions[0][3]);
        positions[0][1] = positions[0][1].relative(eastRelative, 1);
        positions[0][2] = positions[0][2].relative(eastRelative, 1).relative(northRelative, 1);
        positions[0][3] = positions[0][3].relative(northRelative, 1);
        positions[1][0] = positions[1][0].add(0, 1, 0);
        positions[1][1] = positions[1][1].relative(eastRelative, 1).add(0, 1, 0);
        positions[1][2] = positions[1][2].relative(eastRelative, 1).relative(northRelative, 1).add(0, 1, 0);
        positions[1][3] = positions[1][3].relative(northRelative, 1).add(0, 1, 0);
    }

    // Overcomplicated, could just grab position[0][0] -> position[1][2];
    private static double[] getMinMax(Vec3[][] positions) {
        double minX = Double.MAX_VALUE;
        double minY = Double.MAX_VALUE;
        double minZ = Double.MAX_VALUE;
        double maxX = Double.MIN_VALUE;
        double maxY = Double.MIN_VALUE;
        double maxZ = Double.MIN_VALUE;
        for (Vec3[] layer : positions) {
            for (Vec3 pos : layer) {
                minX = Math.min(minX, pos.x);
                minY = Math.min(minY, pos.y);
                minZ = Math.min(minZ, pos.z);
                maxX = Math.max(maxX, pos.x);
                maxY = Math.max(maxY, pos.y);
                maxZ = Math.max(maxZ, pos.z);
            }
        }
        return new double[]{minX, minY, minZ, maxX, maxY, maxZ};
    }

    private void transform() {
        transform(this.positions);
    }

    public float[] getAngle(int layer1, int layer2, int index1, int index2) {
        Vec3 pos1 = this.positions[layer1][index1];
        Vec3 pos2 = this.positions[layer2][index2];
        return Vec3Helper.getDegreesBetweenPoints(pos1, pos2);
    }

    private static boolean validateBox(Vec3[][] positions) {
        Vec3[] bottomPositions = positions[0];
        Vec3[] topPositions = positions[1];
        for (int i = 0; i < topPositions.length; i++) {
            Vec3 bottom = bottomPositions[i];
            Vec3 top = topPositions[i];
            if (bottom == null || top == null) return false;
            if (top.x != bottom.x || top.z != bottom.z) return false;
        }
        return true;
    }

}