package net.refractionapi.refraction.feature.subdivision;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.world.phys.AABB;
import net.refractionapi.refraction.debug.RDebugRenderers;

import java.util.HashSet;

public class GeneratedSet {
    final HashSet<SubdivisionPiece> pieces = new HashSet<>();

    public void addPiece(SubdivisionPiece piece) {
        if (piece == null) return;
        pieces.add(piece);
    }

    public HashSet<SubdivisionPiece> pieces() {
        return pieces;
    }

    public boolean isColliding(BlockPos spawn, Vec3i size) {
        var corner = spawn.offset(size);
        var currBox = new AABB(
                spawn.getX(),
                spawn.getY(),
                spawn.getZ(),
                corner.getX(),
                corner.getY(),
                corner.getZ()
        );
        return this.pieces().stream().anyMatch(piece -> {
            var checkingFirst = piece.spawn;
            var checkingSecond = piece.spawn.offset(size);
            var adjacentBox = new AABB(
                    checkingFirst.getX(),
                    checkingFirst.getY(),
                    checkingFirst.getZ(),
                    checkingSecond.getX(),
                    checkingSecond.getY(),
                    checkingSecond.getZ()
            );
            RDebugRenderers.instance().renderAABB(currBox, 255, 0, 0, 2);
            RDebugRenderers.instance().renderAABB(adjacentBox, 0, 0, 255, 2);
            return currBox.intersects(adjacentBox);
        });
    }
}
