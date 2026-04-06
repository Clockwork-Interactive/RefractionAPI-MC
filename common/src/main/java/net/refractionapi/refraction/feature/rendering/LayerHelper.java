package net.refractionapi.refraction.feature.rendering;

import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.refractionapi.refraction.mixininterfaces.ILivingRenderer;

public class LayerHelper {
    public static void registerOnAll(RenderDispatcherContext context, EntityModelSet modelSet, Layer layer) {
        for (EntityRenderer<? extends Player> renderer : context.playerRenderers().values())
            registerOn(renderer, modelSet, layer);
        for (EntityRenderer<?> renderer : context.renderers().values())
            registerOn(renderer, modelSet, layer);
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    public static void registerOn(EntityRenderer<?> entityRenderer, EntityModelSet modelSet, Layer layer) {
        if (!(entityRenderer instanceof LivingEntityRenderer<?, ?> livingRenderer))
            return;
        if (!(livingRenderer.getModel() instanceof HumanoidModel))
            return;
        if (!(livingRenderer instanceof ILivingRenderer<?, ?> rend))
            return;
        rend.addLayer(layer.create(livingRenderer, modelSet));
    }

    @FunctionalInterface
    public interface Layer<T extends Entity, M extends EntityModel<T>> {
        RenderLayer<T, M> create(LivingEntityRenderer<?, ?> renderer, EntityModelSet set);
    }
}
