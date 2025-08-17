package net.refractionapi.refraction.mixininterfaces;

import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.world.entity.Entity;

public interface ILivingRenderer<T extends Entity, M extends EntityModel<T>> {
    boolean addLayer(RenderLayer<T, M> layer);
}
