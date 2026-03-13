package net.refractionapi.refraction.feature.subdivision.processors;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessor;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessorType;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import net.refractionapi.refraction.feature.subdivision.SubdivisionPiece;
import net.refractionapi.refraction.feature.subdivision.SubdivisionSet;
import org.jetbrains.annotations.Nullable;

public class SubdivisionProcessor extends StructureProcessor {
    final SubdivisionSet set;
    final SubdivisionPiece piece;
    final SubdivisionPiece.Door door;

    public SubdivisionProcessor(SubdivisionSet set, SubdivisionPiece piece, SubdivisionPiece.Door door) {
        this.set = set;
        this.piece = piece;
        this.door = door;
    }

    @Override
    public @Nullable StructureTemplate.StructureBlockInfo processBlock(
            LevelReader level,
            BlockPos offset,
            BlockPos pos,
            StructureTemplate.StructureBlockInfo blockInfo,
            StructureTemplate.StructureBlockInfo currBlock,
            StructurePlaceSettings settings
    ) {
        return currBlock;
    }

    @Override
    protected StructureProcessorType<?> getType() {
        return null;
    }
}
