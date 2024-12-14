package net.refractionapi.refraction.feature.atda;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.refractionapi.refraction.data.Syncable;

public abstract class AtdaData implements Syncable {

    public abstract void save(CompoundTag tag);

    public abstract void load(CompoundTag tag);

    @Override
    public void write(FriendlyByteBuf buf) {
        this.save(new CompoundTag());
    }

    @Override
    public void read(FriendlyByteBuf buf) {
        this.load(buf.readNbt());
    }

}
