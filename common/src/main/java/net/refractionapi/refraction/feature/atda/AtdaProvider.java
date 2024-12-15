package net.refractionapi.refraction.feature.atda;

import net.minecraft.nbt.CompoundTag;
import net.refractionapi.refraction.helper.misc.GenericBuilder;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public abstract class AtdaProvider<E, T extends AtdaData<T>> implements IAtdaProvider, GenericBuilder<T> {

    private T data;

    protected abstract Atda<E, T> getHolder();

    private T getData() {
        return this.data == null ? this.data = build() : this.data;
    }

    @Override
    @SuppressWarnings("unchecked")
    public @NotNull <O, D extends AtdaData<D>> Optional<D> getAtda(Atda<O, D> holder) {
        return holder == this.getHolder() ? (Optional<D>) Optional.of(this.getData()) : Optional.empty();
    }

    @Override
    public void serialize(CompoundTag tag) {
        getData().save(tag);
    }

    @Override
    public void deserialize(CompoundTag tag) {
        getData().load(tag);
    }

}
