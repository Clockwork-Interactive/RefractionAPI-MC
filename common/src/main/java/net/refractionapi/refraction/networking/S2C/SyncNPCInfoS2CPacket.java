package net.refractionapi.refraction.networking.S2C;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Player;
import net.refractionapi.refraction.networking.Packet;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;

public class SyncNPCInfoS2CPacket extends Packet {

    public SyncNPCInfoS2CPacket() {

    }

    public SyncNPCInfoS2CPacket(FriendlyByteBuf buf) {

    }

    @Override
    public void write(FriendlyByteBuf buf) {

    }

    @Override
    public void handle(@Nullable Player player, Consumer<Runnable> context) {

    }

}
