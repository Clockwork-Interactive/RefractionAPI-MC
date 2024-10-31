package net.refractionapi.refraction.networking.S2C;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Player;
import net.refractionapi.refraction.client.ClientData;
import net.refractionapi.refraction.feature.screen.RefractionScreen;
import net.refractionapi.refraction.networking.Packet;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;

public class SendScreenDataS2CPacket extends Packet {

    private final RefractionScreen.Code code;
    private final CompoundTag tag;

    public SendScreenDataS2CPacket(RefractionScreen.Code code, CompoundTag tag) {
        this.code = code;
        this.tag = tag;
    }

    public SendScreenDataS2CPacket(CompoundTag tag) {
        this(RefractionScreen.Code.DATA, tag);
    }

    public SendScreenDataS2CPacket(RefractionScreen.Code code) {
        this(code, new CompoundTag());
    }

    public SendScreenDataS2CPacket(FriendlyByteBuf buf) {
        this.code = RefractionScreen.Code.values()[buf.readVarInt()];
        this.tag = buf.readNbt();
    }

    @Override
    public void write(FriendlyByteBuf buf) {
        buf.writeVarInt(this.code.ordinal());
        buf.writeNbt(this.tag);
    }

    @Override
    public void handle(@Nullable Player player, Consumer<Runnable> context) {
        context.accept(() -> ClientData.screenHandler.handleServerEvent(this.code, this.tag));
    }

}
