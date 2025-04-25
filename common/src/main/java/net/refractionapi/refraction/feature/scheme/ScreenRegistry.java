package net.refractionapi.refraction.feature.scheme;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.refractionapi.refraction.client.ClientData;
import net.refractionapi.refraction.feature.channel.TwoWayChannel;
import net.refractionapi.refraction.helper.clazz.ClazzUtil;
import org.apache.logging.log4j.util.InternalApi;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.UUID;

@InternalApi
public class ScreenRegistry {
    private static final HashMap<Class<? extends ServerScheme>, Class<? extends RScreen>> schemes = new HashMap<>();
    private RScreen screen = null;
    private ScreenScheme<?> scheme = null;
    private Screen previousScreen = null;
    protected TwoWayChannel screenChannel = null;

    /**
     * Use @RegisterScreen
     */
    public static void register(Class<? extends ServerScheme> server, Class<? extends RScreen> client) {
        schemes.put(server, client);
    }

    public static <T extends Screen> T createScreen(ScreenScheme<?> builder, Object[] args) {
        return (T) ClazzUtil.create(schemes.get(builder.schemeClass()), args);
    }

    public void setScreen(@Nullable ScreenScheme<?> scheme, @Nullable Object... creatorArgs) {
        Minecraft.getInstance().setScreen(null);
        this.scheme = scheme;
        this.screen = null;
        if (scheme == null) return;
        Object screen = scheme.clientScreenCreator.apply(scheme, creatorArgs);
        if (screen instanceof Screen && screen instanceof RScreen)
            this.screen = (RScreen) screen;
        else
            throw new IllegalArgumentException("Screen constructor must return an instance of Screen and implement RScreen!");
        this.setScreen((Screen) screen);
    }

    private void setScreen(Screen screen) {
        this.previousScreen = Minecraft.getInstance().screen;
        Minecraft.getInstance().setScreen(screen);
    }

    public int handle(ScreenScheme.ScreenMessage screenMessage) {
        if (screen != null) screen.handle(screenMessage);
        return 1;
    }

    public final int open(ResourceLocation id, UUID uuid, FriendlyByteBuf buf) {
        if (id == null || uuid == null) return 0;
        ScreenScheme<?> scheme = ScreenScheme.builders.get(id);
        if (scheme == null) return 0;
        Object[] args = scheme.deserializer.apply(buf);
        initChannel(uuid);
        setScreen(scheme, args);
        return 1;
    }

    public final int close(boolean server) {
        if (!server && this.screenChannel != null) {
            this.screenChannel.send("default", (buf) -> {
            }, (router, nbt) -> nbt.putInt("code", ScreenScheme.Code.CLOSE.ordinal()));
        } else {
            this.screen = null;
            this.scheme = null;
            if (this.screenChannel != null)
                this.screenChannel.close();
            this.screenChannel = null;
            this.previousScreen = Minecraft.getInstance().screen;
            this.setScreen(null);
        }
        return 1;
    }

    public final int handle(ScreenScheme.Code code, FriendlyByteBuf buf) {
        if (code.equals(ScreenScheme.Code.DATA)) return handle(new ScreenScheme.ScreenMessage(buf));
        else if (code.equals(ScreenScheme.Code.OPEN)) return open(buf.readResourceLocation(), buf.readUUID(), buf);
        else close(true);
        return 1;
    }

    public void initChannel(UUID id) {
        screenChannel = new TwoWayChannel(ClientData.getPlayer().level(), id)
                .registerListener((plr, buf) -> handle(ScreenScheme.Code.values()[screenChannel.header().header().getInt("code")], buf))
                .open();
    }
}
