package net.refractionapi.refraction.helper.registry.block;

import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

public abstract class BaseHorizontalEntityBlock extends BaseHorizontalBlock implements EntityBlock {

    public BaseHorizontalEntityBlock(Properties pProperties) {
        super(pProperties);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level pLevel, BlockState pState, BlockEntityType<T> pBlockEntityType) {
        return (a, b, c, blockEntity) -> {
            if (blockEntity instanceof BaseEntityBlock block) {
                block.tick();
                block.tickCount++;
            }
        };
    }

}
