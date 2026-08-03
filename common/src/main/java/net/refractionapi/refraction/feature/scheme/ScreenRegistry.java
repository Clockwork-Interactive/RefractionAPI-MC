package net.refractionapi.refraction.feature.scheme;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.MenuAccess;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.refractionapi.refraction.client.ClientData;
import net.refractionapi.refraction.feature.channel.TwoWayChannel;
import net.refractionapi.refraction.feature.twc.TWC;
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
    protected TWC screenChannel = null;

    /**
     * Use @RegisterScreen
     */
    public static void register(Class<? extends ServerScheme> server, Class<? extends RScreen> client) {
        schemes.put(server, client);
    }

    public static <T> T createScreen(ScreenScheme<?> builder, Object[] args) {
        return (T) ClazzUtil.create(schemes.get(builder.schemeClass()), args);
    }

    public void setScreen(@Nullable ScreenScheme<?> scheme, @Nullable Object... creatorArgs) {
        Minecraft.getInstance().setScreen(null);
        this.scheme = scheme;
        this.screen = null;
        if (scheme == null) return;
        Object screen = scheme.clientScreenCreator.apply(scheme, creatorArgs);
        if (screen instanceof Screen && screen instanceof RScreen) this.screen = (RScreen) screen;
        else throw new IllegalArgumentException("Screen constructor must implement RScreen!");
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

    public final int open(ResourceLocation id, UUID channelID, FriendlyByteBuf buf) {
        if (id == null || channelID == null) return 0;
        ScreenScheme<?> scheme = ScreenScheme.builders.get(id);
        if (scheme == null) return 0;
        Object[] args = scheme.deserializer.apply(buf);
        initChannel(channelID);
        setScreen(scheme, args);
        return 1;
    }

    public final int openMenu(ResourceLocation id, UUID channelID, FriendlyByteBuf buf) {
        if (id == null || channelID == null) return 0;
        var scheme = ScreenScheme.builders.get(id);
        if (scheme == null) return 0;
        initChannel(channelID);
        var curr = Minecraft.getInstance().screen;
        if (curr instanceof RScreen rScreen && curr instanceof MenuAccess<?>) {
            rScreen.handleMenuInit(new ScreenScheme.ScreenMessage(buf));
            screen = rScreen;
        } else throw new IllegalArgumentException("Current screen is either not MenuAccess or RScreen");
        return 1;
    }

    public final int close(boolean server) {
        if (!server && this.screenChannel != null) {
            var msg = TWC.message();
            msg.headerTag((header) -> header.putInt("code", ScreenScheme.Code.CLOSE.ordinal()));
            this.screenChannel.sendMessage("default", msg);
        } else {
            this.screen = null;
            this.scheme = null;
            if (this.screenChannel != null) this.screenChannel.close();
            this.screenChannel = null;
            this.previousScreen = Minecraft.getInstance().screen;
            this.setScreen(null);
        }
        return 1;
    }

    public final int handle(ScreenScheme.Code code, FriendlyByteBuf buf) {
        return switch (code) {
            case DATA -> handle(new ScreenScheme.ScreenMessage(buf));
            case OPEN -> open(buf.readResourceLocation(), buf.readUUID(), buf);
            case MENU -> openMenu(buf.readResourceLocation(), buf.readUUID(), buf);
            default -> close(true);
        };
    }

    public void initChannel(UUID id) {
        screenChannel = TWC.unnamed(ClientData.getPlayer().level(), id)
                .listener((msg) -> handle(ScreenScheme.Code.values()[msg.headerTag().getInt("code")], msg.buf()))
                .open();
    }
}
