package net.refractionapi.refraction.feature.screen;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.refractionapi.refraction.Refraction;
import net.refractionapi.refraction.client.ClientData;
import net.refractionapi.refraction.data.RefractionData;
import net.refractionapi.refraction.networking.RefractionMessages;
import net.refractionapi.refraction.networking.S2C.SendScreenDataS2CPacket;

import java.util.HashMap;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Function;

/**
 * The ScreenBuilder and it's related classes provide a full server sided control of a screen <br>
 * The example registry of this can be found in the examples folder <br>
 * Registering the auto clientScreenCreator can be found in {@link ClientScreenHandler#init()}
 */
public class ScreenBuilder<T extends ServerScreen> {

    private static final HashMap<String, ScreenBuilder<?>> builders = new HashMap<>();
    private final String id;
    private final Function<Object[], CompoundTag> serializer;
    private final Function<CompoundTag, Object[]> deserializer;
    private final BiFunction<Object[], ScreenBuilder<?>, Object> clientScreenCreator;
    private final BiConsumer<T, CompoundTag> serverHandler;
    private final Function<ServerPlayer, T> serverScreenCreator;
    private final boolean clientAccessible;

    private ScreenBuilder(String id, Function<Object[], CompoundTag> serializer, Function<CompoundTag, Object[]> deserializer, BiFunction<Object[], ScreenBuilder<?>, Object> clientScreenCreator, BiConsumer<T, CompoundTag> serverHandler, Function<ServerPlayer, T> serverScreenCreator, boolean clientAccessible) {
        this.id = id;
        this.serializer = serializer;
        this.deserializer = deserializer;
        this.clientScreenCreator = clientScreenCreator;
        this.serverHandler = serverHandler;
        this.serverScreenCreator = serverScreenCreator;
        this.clientAccessible = clientAccessible;
        builders.put(id, this);
    }

    public String getId() {
        return this.id;
    }

    public CompoundTag getNBTId(Object... args) {
        CompoundTag tag = this.serializer.apply(args);
        tag.putString("builderId", this.id);
        return tag;
    }

    public boolean clientAccessible() {
        return this.clientAccessible;
    }

    public void setScreen(Player player, Object... args) {
        if (!(player instanceof ServerPlayer serverPlayer)) return;
        RefractionData data = RefractionData.get(player);
        data.screen = this.serverScreenCreator.apply(serverPlayer);
        data.screen.init(args);
        data.builder = this;
    }

    public void setScreen(Object... args) {
        if (!this.clientAccessible) {
            Refraction.LOGGER.warn("Screen {} is not client accessible, did you forget to set .clientAccessible()?", this.id);
            return;
        }
        ClientData.screenHandler.openScreen(this, args);
    }

    public void onClose(Player player) {
        if (!(player instanceof ServerPlayer serverPlayer)) return;
        RefractionData data = RefractionData.get(serverPlayer);
        if (data.screen != null) {
            data.screen.onClose();
            data.screen.closeInternal();
        }
        data.screen = null;
        data.builder = null;
    }

    public static void sendCode(Player player, RefractionScreen.Code code) {
        RefractionData data = RefractionData.get(player);
        if (data.screen == null || !(player instanceof ServerPlayer serverPlayer)) return;
        RefractionMessages.sendToPlayer(new SendScreenDataS2CPacket(code), serverPlayer);
    }

    public Object[] deserialize(CompoundTag tag) {
        return this.deserializer.apply(tag);
    }

    public CompoundTag serialize(Object... args) {
        return this.serializer.apply(args);
    }

    public Object createScreen(Object... args) {
        return this.clientScreenCreator.apply(args, this);
    }

    @SuppressWarnings("unchecked")
    public void handleServer(ServerScreen handler, CompoundTag tag) {
        if (!handler.stillValid()) {
            handler.close();
            return;
        }
        this.serverHandler.accept((T) handler, tag);
    }

    @SuppressWarnings("unchecked")
    public static <T extends ServerScreen> ScreenBuilder<T> get(String id) {
        return (ScreenBuilder<T>) builders.get(id);
    }

    public static <T extends ServerScreen> ScreenBuilder<T> get(CompoundTag tag) {
        return get(tag.getString("builderId"));
    }

    public static class Builder {

        private Function<Object[], CompoundTag> serializer = null;
        private Function<CompoundTag, Object[]> deserializer = null;
        private BiFunction<Object[], ScreenBuilder<?>, Object> clientScreenCreator = null;
        private BiConsumer<ServerScreen, CompoundTag> serverHandler = null;
        private Function<ServerPlayer, ServerScreen> serverScreenCreator;
        private boolean clientAccessible = false;

        /**
         * Overrides the default client screen creator
         */
        public Builder clientScreenCreator(BiFunction<Object[], ScreenBuilder<?>, Object> clientScreenCreator) {
            this.clientScreenCreator = clientScreenCreator;
            return this;
        }

        /**
         * Deserializes the CompoundTag into an Object array for construction params
         */
        public Builder deserializer(Function<CompoundTag, Object[]> deserializer) {
            this.deserializer = deserializer;
            return this;
        }

        /**
         * Serializes the CompoundTag into an Object array for later deserialization
         */
        public Builder serializer(Function<Object[], CompoundTag> serializer) {
            this.serializer = serializer;
            return this;
        }

        /**
         * Overrides the default server handling
         */
        public Builder serverHandler(BiConsumer<ServerScreen, CompoundTag> serverHandler) {
            this.serverHandler = serverHandler;
            return this;
        }

        /**
         * Set the server screen for the screen
         */
        public Builder serverScreenCreator(Function<ServerPlayer, ServerScreen> serverScreenCreator) {
            this.serverScreenCreator = serverScreenCreator;
            return this;
        }

        /**
         * Set if the client can call the server to open the screen
         */
        public Builder clientAccessible() {
            this.clientAccessible = true;
            return this;
        }

        @SuppressWarnings("unchecked")
        public <S extends ServerScreen> ScreenBuilder<S> build(String id) {
            if (builders.containsKey(id)) {
                throw new NullPointerException("Duplicate screen id %s".formatted(id));
            }
            if (this.serializer == null) {
                throw new NullPointerException("Serializer must be set");
            }
            if (this.deserializer == null) {
                throw new NullPointerException("Deserializer must be set");
            }
            if (this.clientScreenCreator == null) {
                this.clientScreenCreator = (args, builder) -> ClientData.createScreen(builder, args);
            }
            if (this.serverHandler == null) {
                this.serverHandler = ServerScreen::handle;
            }
            if (this.serverScreenCreator == null) {
                throw new NullPointerException("Server Screen Creator must be set");
            }
            return new ScreenBuilder<>(id, this.serializer, this.deserializer, this.clientScreenCreator, (BiConsumer<S, CompoundTag>) this.serverHandler, (Function<ServerPlayer, S>) this.serverScreenCreator, this.clientAccessible);
        }

    }

}
