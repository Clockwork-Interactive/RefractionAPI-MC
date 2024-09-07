package net.refractionapi.refraction.screen;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.nbt.CompoundTag;
import net.refractionapi.refraction.networking.C2S.SendScreenDataC2SPacket;
import net.refractionapi.refraction.networking.RefractionMessages;
import org.jetbrains.annotations.Nullable;

public class ClientScreenHandler {

    private RefractionScreen screen;
    private ScreenBuilder<?> builder;

    public ClientScreenHandler() {

    }

    public void setScreen(@Nullable ScreenBuilder<?> builder, @Nullable Object... creatorArgs) {
        Minecraft.getInstance().setScreen(null);
        this.builder = builder;
        this.screen = null;
        if (builder == null) return;
        Object screen = builder.createScreen(creatorArgs);
        if (screen instanceof Screen && screen instanceof RefractionScreen) {
            this.screen = (RefractionScreen) screen;
        } else {
            throw new IllegalArgumentException("Screen constructor must return an instance of Screen and implement RefractionScreen");
        }
        Minecraft.getInstance().setScreen((Screen) screen);
    }

    public void onClose(boolean server) {
        this.screen = null;
        this.builder = null;
        if (!server) {
            CompoundTag tag = new CompoundTag();
            tag.putBoolean("close", true);
            RefractionMessages.sendToServer(new SendScreenDataC2SPacket(tag));
        }
    }

    public void handleServerEvent(CompoundTag tag) {
        if (tag.contains("close")) {
            Minecraft.getInstance().setScreen(null);
            this.onClose(true);
            return;
        }
        if (this.screen != null) {
            this.screen.handleServerEvent(tag);
        }
    }

    public void sendData(CompoundTag tag) {
        RefractionMessages.sendToServer(new SendScreenDataC2SPacket(tag));
    }

}
