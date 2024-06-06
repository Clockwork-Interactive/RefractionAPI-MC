package net.refractionapi.refraction.networking.S2C;

import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.registries.ForgeRegistries;
import net.refractionapi.refraction.networking.Packet;
import net.refractionapi.refraction.sound.TrackingSound;

public class TrackingSoundS2CPacket extends Packet {

    private final SoundEvent soundEvent;
    private final int entityId;
    private final boolean looping;
    private final int ticks;

    public TrackingSoundS2CPacket(LivingEntity livingEntity, SoundEvent soundEvent, boolean looping, int loopingTicks) {
        this.soundEvent = soundEvent;
        this.entityId = livingEntity.getId();
        this.looping = looping;
        this.ticks = loopingTicks;
    }

    public TrackingSoundS2CPacket(FriendlyByteBuf friendlyByteBuf) {
        this.soundEvent = friendlyByteBuf.readRegistryId();
        this.entityId = friendlyByteBuf.readInt();
        this.looping = friendlyByteBuf.readBoolean();
        this.ticks = friendlyByteBuf.readInt();
    }

    @Override
    public void toBytes(FriendlyByteBuf friendlyByteBuf) {
        friendlyByteBuf.writeRegistryId(ForgeRegistries.SOUND_EVENTS, this.soundEvent);
        friendlyByteBuf.writeInt(this.entityId);
        friendlyByteBuf.writeBoolean(this.looping);
        friendlyByteBuf.writeInt(this.ticks);
    }

    @Override
    public void handle(NetworkEvent.Context context) {
        context.enqueueWork(() -> {
            LivingEntity livingEntity = (LivingEntity) Minecraft.getInstance().level.getEntity(this.entityId);
            if (livingEntity != null) {
                TrackingSound sound = new TrackingSound(this.soundEvent, livingEntity, this.looping, this.ticks);
                Minecraft.getInstance().getSoundManager().play(sound);
            }
        });
    }
}
