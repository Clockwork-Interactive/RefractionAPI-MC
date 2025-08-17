package net.refractionapi.refraction.feature.examples.rendering;

import net.minecraft.client.model.geom.ModelLayerLocation;
import net.refractionapi.refraction.Refraction;

public class RExampleLayers {
    public static final ModelLayerLocation NVG = register("nvg");

    private static ModelLayerLocation register(String pPath) {
        return register(pPath, "main");
    }

    private static ModelLayerLocation register(String pPath, String pModel) {
        return new ModelLayerLocation(Refraction.id(pPath), pModel);
    }
}
