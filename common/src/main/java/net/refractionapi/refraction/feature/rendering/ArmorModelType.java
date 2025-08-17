package net.refractionapi.refraction.feature.rendering;

import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;

public abstract class ArmorModelType<T extends LivingEntity> extends HumanoidModel<T> {
    public ArmorModelType(ModelPart root) {
        super(root);
    }

    public abstract ResourceLocation texture();
}
