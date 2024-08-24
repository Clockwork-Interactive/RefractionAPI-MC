package net.refractionapi.refraction.capabilities;

import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.common.util.LazyOptional;
import net.refractionapi.refraction.misc.GenericBuilder;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public abstract class Provider<E extends Data<E>> implements ICapabilityProvider, INBTSerializable<CompoundTag>, GenericBuilder<E> {

    protected abstract Capability<E> getData();

    private E data = null;

    private final LazyOptional<E> optional = LazyOptional.of(this::createData);

    private E createData() {
        return this.data == null ? this.data = build() : this.data;
    }

    @Override
    public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        return cap == getData() ? optional.cast() : LazyOptional.empty();
    }

    @Override
    public CompoundTag serializeNBT() {
        CompoundTag tag = new CompoundTag();
        createData().saveNBTData(tag);
        return tag;
    }

    @Override
    public void deserializeNBT(CompoundTag nbt) {
        createData().loadNBTData(nbt);
    }
}
