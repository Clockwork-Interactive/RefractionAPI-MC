package net.refractionapi.refraction.feature.examples.data;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.Entity;
import net.refractionapi.refraction.client.ClientData;
import net.refractionapi.refraction.feature.data.Syncable;

public class SyncedDataExample implements Syncable<SyncedDataExample> {

    private final Entity anEntity;
    private final int anInt;
    private final float aFloat;
    private int nonFinalSyncable = 0;

    public SyncedDataExample(Entity exampleArg1, int exampleArg2, float exampleArg3) {
        this.anEntity = exampleArg1;
        this.anInt = exampleArg2;
        this.aFloat = exampleArg3;
        this.setSynced();
    }

    public SyncedDataExample(FriendlyByteBuf friendlyByteBuf) {
        Object[] synced = this.setSynced().get(this, friendlyByteBuf);
        this.anEntity = (Entity) synced[0];
        this.anInt = (int) synced[1];
        this.aFloat = (float) synced[2];
    }

    @Override
    public void serialize(FriendlyByteBuf buf) {
        buf.writeInt(this.anEntity.getId());
        buf.writeInt(this.anInt);
        buf.writeFloat(this.aFloat);
    }

    @Override
    public Object[] deserialize(FriendlyByteBuf buf) {
        return new Object[]{
                ClientData.getEntity(buf.readInt()),
                buf.readInt(),
                buf.readFloat()
        };
    }

    @Override
    public void write(FriendlyByteBuf buf) {
        buf.writeInt(this.nonFinalSyncable);
    }

    @Override
    public void read(FriendlyByteBuf buf) {
        this.nonFinalSyncable = buf.readInt();
    }
}
