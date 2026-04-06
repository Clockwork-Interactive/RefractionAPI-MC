package net.refractionapi.refraction.feature.atda;

import net.minecraft.nbt.CompoundTag;

public interface IAtdaProvider extends DataHolder {
    default void serialize(CompoundTag tag) {

    }

    default void deserialize(CompoundTag tag) {

    }

    default String getSyncID() {
        return "";
    }
}