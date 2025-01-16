package net.refractionapi.refraction.feature.atda;

import net.minecraft.nbt.CompoundTag;
import net.refractionapi.refraction.helper.misc.GenericBuilder;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public abstract class AtdaProvider<E, D extends AtdaData<D>> implements IAtdaProvider, GenericBuilder<D> {

    private D data;

    protected abstract Atda<E, D> getHolder();

    private D getData() {
        return this.data == null ? this.data = build() : this.data;
    }

    public D copyData() {
        D copy = build();
        copy.copyFrom(getData());
        return copy;
    }

    public D data(E lookup) {
        return readOnly(lookup) ? copyData() : getData();
    }

    @ApiStatus.Internal
    public <O> void tickInternal(O obj) {
        if (getData() == null || this.data == null || this.data.atdaSync == null || this.data.providerSync == null)
            return;
        tick((E) obj);
    }

    public void tick(E obj) {

    }

    public <O> boolean readOnly(O obj) {
        return false;
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
