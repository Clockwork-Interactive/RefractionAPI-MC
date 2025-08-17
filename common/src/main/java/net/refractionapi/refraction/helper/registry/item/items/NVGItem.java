package net.refractionapi.refraction.helper.registry.item.items;

import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.core.Holder;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.refractionapi.refraction.feature.examples.rendering.NVGArmor;
import net.refractionapi.refraction.feature.examples.rendering.RExampleLayers;
import net.refractionapi.refraction.feature.rendering.ArmorRegister;

import java.util.function.Function;

public class NVGItem extends ArmorItem implements ArmorRegister {
    public NVGItem(Holder<ArmorMaterial> material, Type type, Properties properties) {
        super(material, type, properties);
    }

    @Override
    public Function<EntityModelSet, Object> getModel() {
        return (set) -> new NVGArmor<>(set.bakeLayer(RExampleLayers.NVG));
    }
}
