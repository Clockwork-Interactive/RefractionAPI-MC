package net.refractionapi.refraction.networking.S2C;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Player;
import net.refractionapi.refraction.debug.RDebugRenderer;
import net.refractionapi.refraction.networking.Packet;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;

public class DebugRendererS2CPacket extends Packet {

    private final String router;
    private final CompoundTag nbt;

    public DebugRendererS2CPacket(String router, CompoundTag nbt) {
        this.router = router;
        this.nbt = nbt;
    }

    public DebugRendererS2CPacket(FriendlyByteBuf buf) {
        this.router = buf.readUtf();
        this.nbt = buf.readNbt();
    }

    @Override
    public void write(FriendlyByteBuf buf) {
        buf.writeUtf(this.router);
        buf.writeNbt(this.nbt);
    }

    @Override
    public void handle(@Nullable Player player, Consumer<Runnable> context) {
        context.accept(() -> RDebugRenderer.route(this.router, this.nbt));
    }

}
