package net.refractionapi.refraction.mixin;

import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.world.entity.LivingEntity;
import net.refractionapi.refraction.mixininterfaces.ILivingRenderer;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.util.List;

@Mixin(LivingEntityRenderer.class)
public abstract class LivingEntityRendererMixin<T extends LivingEntity, M extends EntityModel<T>> implements ILivingRenderer<T, M> {
    @Shadow @Final protected List<RenderLayer<T, M>> layers;

    @Override
    public boolean addLayer(RenderLayer layer) {
        return this.layers.add(layer);
    }
}
