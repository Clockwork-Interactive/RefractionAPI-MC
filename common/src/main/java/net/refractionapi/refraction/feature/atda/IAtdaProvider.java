package net.refractionapi.refraction.feature.atda;

import net.minecraft.nbt.CompoundTag;

import javax.annotation.Nonnull;
import java.util.Optional;

public interface IAtdaProvider<T extends AtdaData> {

    @Nonnull
    <O, D extends AtdaData>  Optional<T> getAtda(Atda<O, D> holder);

    void serialize(CompoundTag tag);

    void deserialize(CompoundTag tag);

}