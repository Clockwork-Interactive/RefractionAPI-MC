package net.refractionapi.refraction.networking.S2C;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.refractionapi.refraction.client.ClientData;
import net.refractionapi.refraction.networking.Packet;
import net.refractionapi.refraction.feature.sound.TickableSoundRegistry;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;

public class StopTickingSoundS2CPacket extends Packet {
    private final LivingEntity living;
    private final String cacheName;

    public StopTickingSoundS2CPacket(LivingEntity living, String cacheName) {
        this.living = living;
        this.cacheName = cacheName;
    }

    public StopTickingSoundS2CPacket(FriendlyByteBuf buf) {
        this.living = ClientData.getEntity(buf.readInt());
        this.cacheName = buf.readUtf();
    }

    @Override
    public void write(FriendlyByteBuf buf) {
        buf.writeInt(this.living.getId());
        buf.writeUtf(this.cacheName);
    }

    @Override
    public void handle(@Nullable Player player, Consumer<Runnable> context) {
        context.accept(() -> TickableSoundRegistry.stop(this.living, this.cacheName));
    }

}