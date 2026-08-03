package net.refractionapi.refraction.feature.scheme;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.refractionapi.refraction.data.PlrExtension;
import net.refractionapi.refraction.data.TData;
import net.refractionapi.refraction.feature.twc.TWC;
import org.jetbrains.annotations.ApiStatus;

import java.util.function.Consumer;

/**
 * Opens a TWC (TwoWayChannel) to the client screen <br>
 * and handles protocols.
 */
public abstract class ServerScheme {
    protected final ScreenScheme<?> scheme;
    protected final ServerPlayer player;
    protected final TWC channel;

    public <T> ServerScheme(ScreenScheme<T> scheme, ServerPlayer player) {
        this.scheme = scheme;
        this.player = player;
        this.channel = TWC.unnamed(player.level());
        channel.canReceiveFrom((plr) -> plr.equals(player));
        channel.validator((msg) -> {
            // fallback to data anyway --Zeus
            if (!msg.headerTag().contains("code")) return true;
            var code = msg.headerTag().getInt("code");
            return code >= 0 && code < ScreenScheme.Code.values().length;
        });
        channel.listener((msg) -> scheme.handleServer(this, msg));
        channel.open();
    }

    /**
     * If screen is still valid for handling. <br>
     * @return false for termination.
     */
    public boolean stillValid() {
        return true;
    }

    /**
     * If a screen can be opened from the client. <br>
     * Mark with #clientAccessible().
     */
    public boolean canOpen() {
        return stillValid();
    }

    /**
     * Can be closed from the client. <br>
     */
    public boolean canClose() {
        return true;
    }

    public void onClose() {

    }

    @ApiStatus.Internal
    public void close() {
        channel.sendMessage("default", player, TWC.message().headerTag(header -> header.putInt("code", ScreenScheme.Code.CLOSE.ordinal())));
        channel.close();
        onClose();
        TData.get(player, PlrExtension.class).scheme = null;
    }

    @ApiStatus.Internal
    public void handleMsg(ScreenScheme.Code code, FriendlyByteBuf buf) {
        if (!stillValid() || (code.equals(ScreenScheme.Code.CLOSE) && canClose())) {
            close();
            return;
        }
        handleMessage(new ScreenScheme.ScreenMessage(buf));
    }

    public abstract void handleMessage(ScreenScheme.ScreenMessage message);

    public void sendNbt(Consumer<CompoundTag> tag) {
        CompoundTag compoundTag = new CompoundTag();
        tag.accept(compoundTag);
        sendNbt(compoundTag);
    }

    public void sendNbt(CompoundTag tag) {
        sendPacket(ScreenScheme.Code.DATA, (buf) -> buf.writeNbt(tag));
    }

    public void sendBuf(Consumer<FriendlyByteBuf> friendlyByteBuf) {
        sendPacket(ScreenScheme.Code.DATA, friendlyByteBuf);
    }

    public void sendPacket(ScreenScheme.Code code, Consumer<FriendlyByteBuf> consumer) {
        var msg = TWC.message();
        msg.buf(consumer);
        msg.headerTag(header -> header.putInt("code", code.ordinal()));
        channel.sendMessage("default", msg);
    }
}
