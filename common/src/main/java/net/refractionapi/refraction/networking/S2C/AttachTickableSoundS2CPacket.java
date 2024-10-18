package net.refractionapi.refraction.networking.S2C;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.refractionapi.refraction.client.ClientData;
import net.refractionapi.refraction.networking.Packet;
import net.refractionapi.refraction.feature.sound.TickableSoundRegistry;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;

public class AttachTickableSoundS2CPacket extends Packet {

    private final String sound;
    private final LivingEntity livingEntity;
    private final CompoundTag serialized;
    private final String cachedName;

    public AttachTickableSoundS2CPacket(String sound, LivingEntity livingEntity, CompoundTag serialized, String cachedName) {
        this.sound = sound;
        this.livingEntity = livingEntity;
        this.serialized = serialized;
        this.cachedName = cachedName;
    }

    public AttachTickableSoundS2CPacket(FriendlyByteBuf buf) {
        this.sound = buf.readUtf();
        this.livingEntity = ClientData.getEntity(buf.readInt());
        this.serialized = buf.readNbt();
        this.cachedName = buf.readUtf();
    }

    @Override
    public void write(FriendlyByteBuf buf) {
        buf.writeUtf(this.sound);
        buf.writeInt(this.livingEntity.getId());
        buf.writeNbt(this.serialized);
        buf.writeUtf(this.cachedName);
    }

    @Override
    public void handle(@Nullable Player player, Consumer<Runnable> context) {
        context.accept(() ->
                TickableSoundRegistry.attach(this.sound, this.livingEntity, this.serialized, this.cachedName)
        );
    }

}
