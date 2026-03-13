package net.refractionapi.refraction.feature.subdivision.processors;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import net.refractionapi.refraction.feature.subdivision.SubdivisionPiece;
import net.refractionapi.refraction.feature.subdivision.SubdivisionSet;
import net.refractionapi.refraction.helper.registry.RBlocks;
import org.jetbrains.annotations.Nullable;

public class BlockRandomizerProcessor extends SubdivisionProcessor {
    public BlockRandomizerProcessor(SubdivisionSet set, SubdivisionPiece piece, SubdivisionPiece.Door door) {
        super(set, piece, door);
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
        if (set == null) return currBlock;
        if (!currBlock.state().is(RBlocks.RANDOMIZER.get())) return currBlock;
        var newBlock = set.configurer(piece.id()).pullRandomBlock(0);
        if (newBlock == null) return currBlock;
        return new StructureTemplate.StructureBlockInfo(currBlock.pos(), newBlock, currBlock.nbt());
    }
}
