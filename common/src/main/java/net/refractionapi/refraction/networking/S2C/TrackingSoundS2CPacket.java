package net.refractionapi.refraction.networking.S2C;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.refractionapi.refraction.client.ClientData;
import net.refractionapi.refraction.networking.Packet;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;

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
        this.soundEvent = BuiltInRegistries.SOUND_EVENT.get(friendlyByteBuf.readResourceLocation());
        this.entityId = friendlyByteBuf.readInt();
        this.looping = friendlyByteBuf.readBoolean();
        this.ticks = friendlyByteBuf.readInt();
    }

    @Override
    public void write(FriendlyByteBuf friendlyByteBuf) {
        friendlyByteBuf.writeResourceLocation(this.soundEvent.getLocation());
        friendlyByteBuf.writeInt(this.entityId);
        friendlyByteBuf.writeBoolean(this.looping);
        friendlyByteBuf.writeInt(this.ticks);
    }

    @Override
    public void handle(@Nullable Player player, Consumer<Runnable> context) {
        context.accept(() -> ClientData.trackingSound(this.entityId, this.soundEvent, this.looping, this.ticks));
    }

}
