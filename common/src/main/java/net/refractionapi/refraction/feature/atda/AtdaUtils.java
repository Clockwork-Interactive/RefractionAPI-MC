package net.refractionapi.refraction.feature.atda;

import net.minecraft.world.entity.Entity;
import net.refractionapi.refraction.mixininterfaces.IEntity;

public class AtdaUtils {
    public static <O extends Entity, D extends AtdaData<D>> void onClone(O original, O clone, Atda<O, D> holder) {
        if (!(original instanceof IEntity originalEntity) || !(clone instanceof IEntity cloneEntity)) return;
        originalEntity.getAtda(holder).ifPresent(data -> cloneEntity.getAtda(holder).ifPresent(cloneData -> cloneData.copyFrom(data)));
    }

    // inexplicit requirement that atdaObj implements IAtdaProvider
    // because we're using mixin shenanigans to add IAtdaProvider to existing classes --Zeus
    public static <O, I extends IAtdaProvider> void attachAtda(O atdaObj, Atda<O, ?> holder, I provider) {
        if (atdaObj == null) return;
        if (!(atdaObj instanceof IAtdaProvider atda))
            throw new IllegalArgumentException("Object %s does not implement IAtdaProvider".formatted(atdaObj.toString()));
        if (atda.getAtda(holder).isPresent()) return;
        atda.addData(holder, provider);
    }
}
