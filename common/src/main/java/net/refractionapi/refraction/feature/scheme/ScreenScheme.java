package net.refractionapi.refraction.feature.scheme;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.refractionapi.refraction.Refraction;
import net.refractionapi.refraction.client.RefractionClient;
import net.refractionapi.refraction.data.PlrExtension;
import net.refractionapi.refraction.data.RefractionData;
import net.refractionapi.refraction.data.TData;
import net.refractionapi.refraction.feature.channel.NamedAPI;
import net.refractionapi.refraction.feature.channel.ThreadedAPI;
import net.refractionapi.refraction.feature.channel.TwoWayChannel;
import net.refractionapi.refraction.helper.clazz.RModRegistrar;
import org.apache.logging.log4j.util.TriConsumer;

import java.util.HashMap;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Function;

/**
 * Updated system of the old @ServerBuilder <br>
 * Registration example in @ExampleScreenRegistry <br>
 * Use @RegisterScreen annotation on client screens <br>
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
    protected final Function<ServerPlayer, ServerScheme> serverScreenCreator;
    protected final boolean clientAccessible;
    static ResourceLocation SCREEN_ID = Refraction.id("screen");
    static ThreadedAPI SCREEN_API = NamedAPI.create(SCREEN_ID)
            .configureClient(twc -> twc.registerListener((plr, buf) -> RefractionClient.screenRegistry.handle(Code.OPEN, buf)))
            .configureServer(twc -> twc.registerListener(ScreenScheme::handleOpen))
            .initCommon();

    private ScreenScheme(
            ResourceLocation id,
            BiConsumer<Object[], FriendlyByteBuf> serializer,
            Function<FriendlyByteBuf, Object[]> deserializer,
            BiFunction<ScreenScheme<?>, Object[], Object> clientScreenCreator,
            Class<? extends ServerScheme> schemeClass,
            TriConsumer<ServerScheme, Code, FriendlyByteBuf> serverHandler,
            Function<ServerPlayer, ServerScheme> serverScreenCreator,
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

    protected int handleServer(ServerScheme screen, TwoWayChannel.ReceivedHeader header, FriendlyByteBuf buf) {
        Code code = header.header().contains("code") ? Code.values()[header.header().getInt("code")] : Code.DATA;
        this.serverHandler.accept(screen, code, buf);
        return 0;
    }

    protected static int handleOpen(Player player, FriendlyByteBuf buf) {
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
        ServerScheme scheme = this.serverScreenCreator.apply(serverPlayer);
        if (scheme == null || !scheme.canOpen()) return null;
        PlrExtension data = data(player);
        if (data.scheme != null) data.scheme.close();
        data.scheme = scheme;
        SCREEN_API.channel().send(
                player,
                "default",
                (friendlyByteBuf) -> {
                    friendlyByteBuf.writeResourceLocation(this.id);
                    friendlyByteBuf.writeUUID(scheme.channel.id());
                    this.serializer.accept(args, friendlyByteBuf);
                },
                (rt, ct) -> ct.putInt("code", 2)
        );
        return (T) scheme;
    }

    /**
     * Used to open the screen from the client. <br>
     * If marked with #clientAccessible().
     */
    public void open(Object... args) {
        SCREEN_API.channel().send(
                "default",
                (friendlyByteBuf) -> {
                    friendlyByteBuf.writeResourceLocation(this.id);
                    this.serializer.accept(args, friendlyByteBuf);
                },
                (rt, ct) -> ct.putInt("code", 2)
        );
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
        OPEN;

        Code() {
        }
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
        private Function<ServerPlayer, ServerScheme> serverScreenCreator;
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

        public Builder<T> serverScreenCreator(Class<? extends ServerScheme> schemeClass, Function<ServerPlayer, ServerScheme> serverScreenCreator) {
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
