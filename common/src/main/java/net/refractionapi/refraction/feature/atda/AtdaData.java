package net.refractionapi.refraction.feature.atda;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.entity.Entity;
import net.refractionapi.refraction.feature.data.Syncable;

public abstract class AtdaData<T extends AtdaData<T>> implements Syncable<T> {
    IAtdaProvider providerSync;
    Atda<?, ?> atdaSync;
    boolean first = true;

    public AtdaData() {
        this.setSynced();
    }

    AtdaData<T> setSyncables(IAtdaProvider providerSync, Atda<?, ?> atda) {
        this.providerSync = providerSync;
        this.atdaSync = atda;
        return this;
    }

    public void onFirstLoad(CompoundTag tag) {

    }

    public void onAttach(Object owner, MinecraftServer server) {

    }

    public abstract void save(CompoundTag tag);

    public abstract void load(CompoundTag tag);

    @Override
    public void write(FriendlyByteBuf buf) {
        CompoundTag tag = new CompoundTag();
        this.save(tag);
        buf.writeUtf(this.providerSync.getSyncID() + this.getClass().getName());
        buf.writeUtf(this.atdaSync.id.toString());
        buf.writeNbt(tag);
    }

    @Override
    public void read(FriendlyByteBuf buf) {
        String syncID = buf.readUtf();
        String atdaID = buf.readUtf();
        Atda<?, ?> atda = Atda.fromMap(ResourceLocation.parse(atdaID));
        if (atda == null) return;
        this.load(buf.readNbt());
        atda.clientLookup.put(syncID, this);
    }

    public abstract void copyFrom(T data);
}
