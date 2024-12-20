package net.refractionapi.refraction.feature.atda;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.Level;

import javax.annotation.Nonnull;
import java.util.Optional;

public interface IAtdaProvider {

    @Nonnull
    <O, D extends AtdaData<D>> Optional<D> getAtda(Atda<O, D> holder);

    default void serialize(CompoundTag tag) {

    }

    default void deserialize(CompoundTag tag) {

    }

    default String getSyncID() {
        return "";
    }

    default Level getLevel() {
        return null;
    }

}