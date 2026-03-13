package net.refractionapi.refraction.feature.subdivision.processors;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import net.refractionapi.refraction.feature.subdivision.SubdivisionPiece;
import net.refractionapi.refraction.feature.subdivision.SubdivisionSet;
import net.refractionapi.refraction.helper.registry.RBlocks;
import org.jetbrains.annotations.Nullable;

public class DoorReplacementProcessor extends SubdivisionProcessor {
    public DoorReplacementProcessor(SubdivisionSet set, SubdivisionPiece piece, SubdivisionPiece.Door door) {
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
        if (!currBlock.state().is(RBlocks.DOORWAY.get())) return currBlock;
        return new StructureTemplate.StructureBlockInfo(currBlock.pos(), Blocks.AIR.defaultBlockState(), new CompoundTag());
    }
}
