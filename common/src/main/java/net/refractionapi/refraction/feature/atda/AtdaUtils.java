package net.refractionapi.refraction.feature.atda;

import net.refractionapi.refraction.mixininterfaces.IEntity;

public class AtdaUtils {
    public static <O, D extends AtdaData<D>> void onClone(O original, O clone, Atda<O, D> holder) {
        if (original instanceof IEntity originalEntity && clone instanceof IEntity cloneEntity) {
            originalEntity.getAtda(holder).ifPresent(data -> cloneEntity.getAtda(holder).ifPresent(cloneData -> cloneData.copyFrom(data)));
        }
    }

    public static <O, I extends IAtdaProvider> void attachAtda(O entity, Atda<O, ?> holder, I provider) {
        if (entity instanceof IEntity atda && atda.getAtda(holder).isEmpty()) {
            atda.addData(holder, provider);
        }
    }
}
