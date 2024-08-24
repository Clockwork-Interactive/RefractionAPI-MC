package net.refractionapi.refraction.capabilities;

import net.minecraft.nbt.CompoundTag;

public abstract class Data<T extends Data<T>> {

    public abstract void saveNBTData(CompoundTag tag);

    public abstract void loadNBTData(CompoundTag tag);

    public abstract void copyFromData(T data);

}
