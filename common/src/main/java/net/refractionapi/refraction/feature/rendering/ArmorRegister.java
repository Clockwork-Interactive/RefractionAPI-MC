package net.refractionapi.refraction.feature.rendering;

import net.minecraft.client.model.geom.EntityModelSet;

import java.util.function.Function;

public interface ArmorRegister {
    /**
     * Object hides client side object in common env <br>
     * Expected ArmorModelType
     */
    Function<EntityModelSet, Object> getModel();
}
