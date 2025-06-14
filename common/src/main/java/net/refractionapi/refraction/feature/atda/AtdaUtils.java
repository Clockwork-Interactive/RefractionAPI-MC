package net.refractionapi.refraction.feature.atda;

import net.minecraft.world.level.Level;
import net.refractionapi.refraction.mixininterfaces.IEntity;
import net.refractionapi.refraction.mixininterfaces.ILevel;

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

    public static <O extends Level, I extends IAtdaProvider> void attachAtda(O level, Atda<O, ?> holder, I provider) {
        if (level instanceof ILevel atda && atda.getAtda(holder).isEmpty()) {
            atda.addData(holder, provider);
        }
    }
}
