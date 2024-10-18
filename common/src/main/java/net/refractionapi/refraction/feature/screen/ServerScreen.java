package net.refractionapi.refraction.feature.screen;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.refractionapi.refraction.networking.RefractionMessages;
import net.refractionapi.refraction.networking.S2C.SendScreenDataS2CPacket;
import net.refractionapi.refraction.networking.S2C.SetScreenS2CPacket;

public abstract class ServerScreen {

    private final ScreenBuilder<?> builder;
    private final ServerPlayer player;

    public ServerScreen(ScreenBuilder<?> builder, ServerPlayer player) {
        this.builder = builder;
        this.player = player;
    }

    public void init(Object... args) {
        RefractionMessages.sendToPlayer(new SetScreenS2CPacket(this.builder, this.builder.serialize(args)), this.player);
    }

    public boolean stillValid() {
        return this.player.isAlive();
    }

    public void onClose() {

    }

    public void close() {
        this.builder.onClose(this.player);
        CompoundTag tag = new CompoundTag();
        tag.putBoolean("close", true);
        RefractionMessages.sendToPlayer(new SendScreenDataS2CPacket(tag), this.player);
    }

    public void sendData(CompoundTag tag) {
        RefractionMessages.sendToPlayer(new SendScreenDataS2CPacket(tag), this.player);
    }

    public abstract void handle(CompoundTag tag);

    public ServerPlayer getPlayer() {
        return this.player;
    }

    public ScreenBuilder<?> getBuilder() {
        return this.builder;
    }

}
