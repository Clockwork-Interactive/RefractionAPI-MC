package net.refractionapi.refraction.feature.scheme;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Player;
import net.refractionapi.refraction.Refraction;
import net.refractionapi.refraction.client.RefractionClient;
import net.refractionapi.refraction.data.PlrExtension;
import net.refractionapi.refraction.data.TData;
import net.refractionapi.refraction.events.Scheduler;
import net.refractionapi.refraction.feature.twc.TWC;
import net.refractionapi.refraction.helper.clazz.RModRegistrar;
import org.apache.logging.log4j.util.TriConsumer;

import java.util.HashMap;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * Updated system of the old @ServerBuilder <br>
 * Registration example in @ExampleScreenRegistry <br>
 * Use @RegisterScreen annotation on client screens <br>
 *
 * @param <T>
 */
public class ScreenScheme<T> {
    protected static final HashMap<ResourceLocation, ScreenScheme<?>> builders = new HashMap<>();
    protected final ResourceLocation id;
    protected final BiConsumer<Object[], FriendlyByteBuf> serializer;
    protected final Function<FriendlyByteBuf, Object[]> deserializer;
    protected final BiFunction<ScreenScheme<?>, Object[], Object> clientScreenCreator;
    private final Class<? extends ServerScheme> schemeClass;
    protected final TriConsumer<ServerScheme, Code, FriendlyByteBuf> serverHandler;
    protected final ScreenCreator serverScreenCreator;
    protected final boolean clientAccessible;
    static ResourceLocation SCREEN_ID = Refraction.id("screen");
    static TWC.Sided SCREEN_API = TWC.named(SCREEN_ID)
            .configureClient(twc -> {
                twc.listener((msg) -> RefractionClient.screenRegistry.handle(Code.OPEN, msg.buf()));
                twc.listener("menu", (msg) -> RefractionClient.screenRegistry.handle(Code.MENU, msg.buf()));
            })
            .configureServer(twc -> twc.listener(ScreenScheme::handleOpen))
            .initCommon();

    private ScreenScheme(
            ResourceLocation id,
            BiConsumer<Object[], FriendlyByteBuf> serializer,
            Function<FriendlyByteBuf, Object[]> deserializer,
            BiFunction<ScreenScheme<?>, Object[], Object> clientScreenCreator,
            Class<? extends ServerScheme> schemeClass,
            TriConsumer<ServerScheme, Code, FriendlyByteBuf> serverHandler,
            ScreenCreator serverScreenCreator,
            boolean clientAccessible
    ) {
        if (builders.containsKey(id)) {
            throw new RuntimeException("Duplicate screen id %s".formatted(id));
        }
        this.id = id;
        this.serializer = serializer;
        this.deserializer = deserializer;
        this.clientScreenCreator = clientScreenCreator;
        this.schemeClass = schemeClass;
        this.serverHandler = serverHandler;
        this.serverScreenCreator = serverScreenCreator;
        this.clientAccessible = clientAccessible;
        builders.put(id, this);
    }

    protected int handleServer(ServerScheme screen, TWC.Message message) {
        var header = message.headerTag();
        var buf = message.buf();
        var code = header.contains("code") ? Code.values()[header.getInt("code")] : Code.DATA;
        this.serverHandler.accept(screen, code, buf);
        return 0;
    }

    protected static int handleOpen(TWC.Message message) {
        var player = message.player();
        var buf = message.buf();
        if (data(player).scheme != null || !(player instanceof ServerPlayer serverPlayer)) return 0;
        ScreenScheme<?> scheme = builders.get(buf.readResourceLocation());
        if (scheme == null || !scheme.clientAccessible) {
            Refraction.LOGGER.warn(
                    "{} ({}) tried accessing screen which is not #clientAccessible or is invalid - {} ",
                    serverPlayer.getStringUUID(), serverPlayer.getName().getString(), scheme == null ? "null" : scheme.id.toString()
            );
            return 0;
        }
        scheme.open(player, scheme.deserializer.apply(buf));
        return 1;
    }

    protected static PlrExtension data(Player player) {
        return TData.get(player, PlrExtension.class);
    }

    protected Class<? extends ServerScheme> schemeClass() {
        return schemeClass;
    }

    /**
     * Used to open the screen from the server.
     */
    public T open(Player player, Object... args) {
        if (!(player instanceof ServerPlayer serverPlayer)) return null;
        ServerScheme scheme = this.serverScreenCreator.create(serverPlayer, args);
        if (scheme == null || !scheme.canOpen()) return null;
        PlrExtension data = data(player);
        if (data.scheme != null) data.scheme.close();
        data.scheme = scheme;
        sendPacket(serverPlayer, Code.OPEN, (buf) -> {
            buf.writeResourceLocation(this.id);
            buf.writeUUID(scheme.channel.id());
            this.serializer.accept(args, buf);
        });
        return (T) scheme;
    }

    public T openMenu(Player player, MenuProvider provider, Object... args) {
        if (!(player instanceof ServerPlayer serverPlayer)) return null;
        var scheme = this.serverScreenCreator.create(serverPlayer, args);
        if (scheme == null || !scheme.canOpen()) return null;
        var opt = serverPlayer.openMenu(provider);
        if (opt.isEmpty()) {
            scheme.close();
            return null;
        }
        var data = data(player);
        if (data.scheme != null) data.scheme.close();
        data.scheme = scheme;
        Scheduler.addTask(false, () -> sendPacket(serverPlayer, "menu", Code.MENU, (buf) -> {
            buf.writeResourceLocation(id);
            buf.writeUUID(scheme.channel.id());
            serializer.accept(args, buf);
        }));
        return (T) scheme;
    }

    public void sendPacket(ServerPlayer serverPlayer, String router, Code code, Consumer<FriendlyByteBuf> consumer) {
        var msg = TWC.message();
        msg.buf(consumer);
        msg.headerTag((header) -> header.putInt("code", code.ordinal()));
        SCREEN_API.sendMessage(router, serverPlayer, msg);
    }

    public void sendPacket(ServerPlayer serverPlayer, Code code, Consumer<FriendlyByteBuf> consumer) {
        sendPacket(serverPlayer, "default", code, consumer);
    }

    /**
     * Used to open the screen from the client. <br>
     * If marked with #clientAccessible().
     */
    public void open(Object... args) {
        var msg = TWC.message();
        msg.buf((buf) -> {
            buf.writeResourceLocation(id);
            serializer.accept(args, buf);
        });
        msg.headerTag((header) -> header.putInt("code", Code.OPEN.ordinal()));
        SCREEN_API.sendMessage("default", msg);
    }

    public static <T extends ServerScheme> Builder<T> builder() {
        return new Builder<>();
    }

    public record ScreenMessage(FriendlyByteBuf buf) {
        public CompoundTag tag() {
            return buf.readNbt();
        }
    }

    public enum Code {
        DATA,
        CLOSE,
        OPEN,
        MENU;

        Code() {
        }
    }

    @FunctionalInterface
    public interface ScreenCreator {
        ServerScheme create(ServerPlayer player, Object... args);
    }

    /**
     * Most overrides are just for convenience. <br>
     * Default handling is good for 99% of cases.
     */
    public static class Builder<T extends ServerScheme> {
        private BiConsumer<Object[], FriendlyByteBuf> serializer = (args, buf) -> {
            // default serializer does nothing --Zeus
        };
        private Function<FriendlyByteBuf, Object[]> deserializer = (buf -> {
            // default deserializer does nothing --Zeus
            return new Object[0];
        });
        private BiFunction<ScreenScheme<?>, Object[], Object> clientScreenCreator = ScreenRegistry::createScreen;
        private TriConsumer<ServerScheme, Code, FriendlyByteBuf> serverHandler = ServerScheme::handleMsg;
        private ScreenCreator serverScreenCreator;
        private Class<? extends ServerScheme> schemeClass;
        private boolean clientAccessible = false;

        private Builder() {

        }

        public Builder<T> serializer(BiConsumer<Object[], FriendlyByteBuf> serializer) {
            this.serializer = serializer;
            return this;
        }

        public Builder<T> nbtSerializer(BiConsumer<Object[], CompoundTag> serializer) {
            this.serializer = (objs, buf) -> {
                CompoundTag tag = new CompoundTag();
                serializer.accept(objs, tag);
                buf.writeNbt(tag);
            };
            return this;
        }

        public Builder<T> deserializer(Function<FriendlyByteBuf, Object[]> deserializer) {
            this.deserializer = deserializer;
            return this;
        }

        public Builder<T> nbtDeserializer(Function<CompoundTag, Object[]> serializer) {
            this.deserializer = (friendlyByteBuf) -> serializer.apply(friendlyByteBuf.readNbt());
            return this;
        }

        /**
         * If you want to override @RegisterScreen
         */
        public Builder<T> clientScreenCreator(BiFunction<ScreenScheme<?>, Object[], Object> clientScreenCreator) {
            this.clientScreenCreator = clientScreenCreator;
            return this;
        }

        public Builder<T> serverHandler(TriConsumer<ServerScheme, Code, FriendlyByteBuf> serverHandler) {
            this.serverHandler = serverHandler;
            return this;
        }

        public Builder<T> serverScreenCreator(Class<? extends ServerScheme> schemeClass, ScreenCreator serverScreenCreator) {
            this.serverScreenCreator = serverScreenCreator;
            this.schemeClass = schemeClass;
            return this;
        }

        /**
         * Client can call server creation of screens.
         */
        public Builder<T> clientAccessible() {
            this.clientAccessible = true;
            return this;
        }

        public <S extends ServerScheme> ScreenScheme<S> build(String id) {
            if (serverScreenCreator == null) {
                throw new RuntimeException("Server Screen Creator can't be null for id %s!".formatted(id));
            }
            return new ScreenScheme<>(
                    RModRegistrar.id(id, 2),
                    serializer,
                    deserializer,
                    clientScreenCreator,
                    schemeClass,
                    serverHandler,
                    serverScreenCreator,
                    clientAccessible
            );
        }
    }
}
