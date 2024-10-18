package net.refractionapi.refraction.networking.C2S;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Player;
import net.refractionapi.refraction.feature.interaction.InteractionBuilder;
import net.refractionapi.refraction.networking.Packet;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;

public class SyncInteractionC2SPacket extends Packet {

    private final String id;
    private final CompoundTag tag;

    public SyncInteractionC2SPacket(String id, CompoundTag tag) {
        this.id = id;
        this.tag = tag;
    }

    public SyncInteractionC2SPacket(FriendlyByteBuf buf) {
        this.id = buf.readUtf();
        this.tag = buf.readNbt();
    }

    @Override
    public void write(FriendlyByteBuf buf) {
        buf.writeUtf(this.id);
        buf.writeNbt(this.tag);
    }

    @Override
    public void handle(@Nullable Player player, Consumer<Runnable> context) {
        context.accept(() -> InteractionBuilder.getBuilder(this.id).ifPresent((builder) -> builder.handleServer(player, this.tag)));
    }

}
