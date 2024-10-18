package net.refractionapi.refraction.networking.S2C;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Player;
import net.refractionapi.refraction.client.ClientData;
import net.refractionapi.refraction.networking.Packet;
import net.refractionapi.refraction.feature.screen.ScreenBuilder;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;

public class SetScreenS2CPacket extends Packet {

    private final ScreenBuilder<?> builder;
    private final CompoundTag serialized;

    public SetScreenS2CPacket(ScreenBuilder<?> builder, CompoundTag serialized) {
        this.builder = builder;
        this.serialized = serialized;
    }

    public SetScreenS2CPacket(FriendlyByteBuf buf) {
        this.builder = ScreenBuilder.get(buf.readUtf());
        this.serialized = buf.readNbt();
    }

    @Override
    public void write(FriendlyByteBuf buf) {
        buf.writeUtf(this.builder.getId());
        buf.writeNbt(this.serialized);
    }

    @Override
    public void handle(@Nullable Player player, Consumer<Runnable> context) {
        context.accept(() -> ClientData.screenHandler.setScreen(this.builder, this.builder.deserialize(this.serialized)));
    }

}
