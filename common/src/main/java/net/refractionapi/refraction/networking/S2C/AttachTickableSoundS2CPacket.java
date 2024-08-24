package net.refractionapi.refraction.networking.S2C;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Player;
import net.refractionapi.refraction.networking.Packet;
import net.refractionapi.refraction.sound.TickableSoundRegistry;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;

public class AttachTickableSoundS2CPacket extends Packet {

    private final String sound;
    private final CompoundTag serialized;

    public AttachTickableSoundS2CPacket(String sound, CompoundTag serialized) {
        this.sound = sound;
        this.serialized = serialized;
    }

    public AttachTickableSoundS2CPacket(FriendlyByteBuf buf) {
        this.sound = buf.readUtf();
        this.serialized = buf.readNbt();
    }

    @Override
    public void write(FriendlyByteBuf buf) {
        buf.writeUtf(this.sound);
        buf.writeNbt(this.serialized);
    }

    @Override
    public void handle(@Nullable Player player, Consumer<Runnable> context) {
        context.accept(() ->
                TickableSoundRegistry.attach(this.sound, this.serialized)
        );
    }

}
