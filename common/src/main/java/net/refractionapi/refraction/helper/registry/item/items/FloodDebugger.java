package net.refractionapi.refraction.helper.registry.item.items;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.phys.AABB;
import net.refractionapi.refraction.debug.RDebugRenderers;
import net.refractionapi.refraction.feature.algorithm.FloodFiller;

public class FloodDebugger extends Item {

    public FloodDebugger(Properties pProperties) {
        super(pProperties);
    }

    @Override
    public InteractionResult useOn(UseOnContext pContext) {
        if (!(pContext.getLevel() instanceof ServerLevel serverLevel)) return super.useOn(pContext);
        BlockPos pos = pContext.getClickedPos();
        FloodFiller floodFiller = FloodFiller.create(pContext.getLevel(), pos);
        floodFiller.setMaxSpread(10);
        floodFiller.setDirections(FloodFiller.horizontal());
        AABB aabb = floodFiller.floodFill().createBoundingBox();
        RDebugRenderers.renderAABB(aabb, 255, 0, 0, 5, serverLevel);
        return super.useOn(pContext);
    }

}
