package net.refractionapi.refraction.feature.screen;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.nbt.CompoundTag;
import net.refractionapi.refraction.feature.examples.screen.ExampleScreen;
import net.refractionapi.refraction.feature.examples.screen.ExampleScreenRegistry;
import net.refractionapi.refraction.networking.C2S.SendScreenDataC2SPacket;
import net.refractionapi.refraction.networking.RefractionMessages;

import javax.annotation.Nullable;
import java.util.HashMap;

public class ClientScreenHandler {

    private RefractionScreen screen;
    private ScreenBuilder<?> builder;
    private Screen previousScreen = null;
    private Screen closedScreen = null;
    private static final HashMap<ScreenBuilder<?>, Class<? extends Screen>> sceens = new HashMap<>();

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
        this.setScreen((Screen) screen);
    }

    public void openScreen(ScreenBuilder<?> builder, Object... args) {
        RefractionMessages.sendToServer(new SendScreenDataC2SPacket(RefractionScreen.Code.OPEN, builder.getNBTId(args)));
    }

    public void onClose(boolean server) {
        if (!server) {
            RefractionMessages.sendToServer(new SendScreenDataC2SPacket(RefractionScreen.Code.CLOSE));
        } else {
            this.screen = null;
            this.builder = null;
            this.closedScreen = Minecraft.getInstance().screen;
            this.setScreen(null);
        }
    }

    public void reopenScreen() {
        if (this.closedScreen != null) {
            this.setScreen(this.closedScreen);
            this.closedScreen = null;
        }
    }

    public void handleServerEvent(RefractionScreen.Code code, CompoundTag tag) {
        if (code.equals(RefractionScreen.Code.CLOSE)) {
            this.onClose(true);
            return;
        } else if (code.equals(RefractionScreen.Code.REOPEN)) {
            this.reopenScreen();
            return;
        }
        if (this.screen != null) {
            this.screen.handleServerEvent(code, tag);
        }
    }

    private void setScreen(Screen screen) {
        this.previousScreen = Minecraft.getInstance().screen;
        Minecraft.getInstance().setScreen(screen);
    }

    public Screen getPreviousScreen() {
        return this.previousScreen;
    }

    public void sendData(CompoundTag tag) {
        RefractionMessages.sendToServer(new SendScreenDataC2SPacket(tag));
    }

    public static void registerScreen(ScreenBuilder<?> builder, Class<? extends Screen> screen) {
        sceens.put(builder, screen);
    }

    public static Class<? extends Screen> getScreen(ScreenBuilder<?> builder) {
        return sceens.get(builder);
    }

    public static void init() {
        registerScreen(ExampleScreenRegistry.EXAMPLE_SCREEN, ExampleScreen.class);
    }

}
