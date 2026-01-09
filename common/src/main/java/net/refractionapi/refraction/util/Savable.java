package net.refractionapi.refraction.util;

import net.minecraft.nbt.CompoundTag;

public interface Savable {
    CompoundTag save();

    void load(CompoundTag tag);
}
