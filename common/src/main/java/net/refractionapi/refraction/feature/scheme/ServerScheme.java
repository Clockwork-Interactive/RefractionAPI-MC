package net.refractionapi.refraction.feature.scheme;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.refractionapi.refraction.data.PlrExtension;
import net.refractionapi.refraction.data.TData;
import net.refractionapi.refraction.feature.channel.TwoWayChannel;
import org.jetbrains.annotations.ApiStatus;

import java.util.function.Consumer;

/**
 * Opens a TWC (TwoWayChannel) to the client screen <br>
 * and handles protocols.
 */
public abstract class ServerScheme {
    protected final ScreenScheme<?> scheme;
    protected final ServerPlayer player;
    protected final TwoWayChannel channel;

    public <T> ServerScheme(ScreenScheme<T> scheme, ServerPlayer player) {
        this.scheme = scheme;
        this.player = player;
        this.channel = new TwoWayChannel(player.level());
        channel.registerListener((plr, buf) -> scheme.handleServer(this, channel.header(), buf))
                .owner(player)
                .open();
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
        channel.send("default", (buf) -> {
        }, (router, ct) -> ct.putInt("code", ScreenScheme.Code.CLOSE.ordinal()));
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

    public void sendNbt(CompoundTag tag) {
        channel.send("default", (buf) -> buf.writeNbt(tag), (rt, ct) -> ct.putInt("code", 0));
    }

    public void sendNbt(Consumer<CompoundTag> tag) {
        CompoundTag compoundTag = new CompoundTag();
        tag.accept(compoundTag);
        sendNbt(compoundTag);
    }

    public void sendBuf(Consumer<FriendlyByteBuf> friendlyByteBuf) {
        channel.send("default", friendlyByteBuf::accept, (rt, ct) -> ct.putInt("code", 0));
    }
}
