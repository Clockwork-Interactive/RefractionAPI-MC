package net.refractionapi.refraction.feature.examples.atda;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.refractionapi.refraction.feature.atda.Atda;
import net.refractionapi.refraction.feature.atda.AtdaProvider;

public class AtdaExampleProvider extends AtdaProvider<LivingEntity, AtdaExampleData> {
    @Override
    protected Atda<LivingEntity, AtdaExampleData> getHolder() {
        return AtdaExampleRegistry.EXAMPLE;
    }

    @Override
    public AtdaExampleData build() {
        return new AtdaExampleData();
    }

    @Override
    public void tick(LivingEntity obj) {
        // you can also tick atda!
    }

    @Override
    public <E> boolean readOnly(E obj) {
        return ((Entity) obj).level().isClientSide;
    }
}
