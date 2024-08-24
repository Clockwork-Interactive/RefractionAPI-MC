package net.refractionapi.refraction.screen;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.refractionapi.refraction.data.RefractionData;

import java.util.HashMap;
import java.util.function.BiConsumer;
import java.util.function.Function;

public class ScreenBuilder<T extends ServerScreen> {

    private static final HashMap<String, ScreenBuilder<?>> builders = new HashMap<>();
    private final String id;
    private final Function<Object[], CompoundTag> serializer;
    private final Function<CompoundTag, Object[]> deserializer;
    private final Function<Object[], Object> clientScreenCreator;
    private final BiConsumer<T, CompoundTag> serverHandler;
    private final Function<ServerPlayer, T> serverScreenCreator;

    private ScreenBuilder(String id, Function<Object[], CompoundTag> serializer, Function<CompoundTag, Object[]> deserializer, Function<Object[], Object> clientScreenCreator, BiConsumer<T, CompoundTag> serverHandler, Function<ServerPlayer, T> serverScreenCreator) {
        this.id = id;
        this.serializer = serializer;
        this.deserializer = deserializer;
        this.clientScreenCreator = clientScreenCreator;
        this.serverHandler = serverHandler;
        this.serverScreenCreator = serverScreenCreator;
        builders.put(id, this);
    }

    public String getId() {
        return this.id;
    }

    public void setScreen(Player player, Object... args) {
        if (!(player instanceof ServerPlayer serverPlayer)) return;
        RefractionData data = RefractionData.get(player);
        data.screen = this.serverScreenCreator.apply(serverPlayer);
        data.screen.init(args);
        data.builder = this;
    }

    public void onClose(Player player) {
        if (!(player instanceof ServerPlayer serverPlayer)) return;
        RefractionData data = RefractionData.get(serverPlayer);
        if (data.screen != null) {
            data.screen.onClose();
        }
        data.screen = null;
        data.builder = null;
    }

    public Object[] deserialize(CompoundTag tag) {
        return this.deserializer.apply(tag);
    }

    public CompoundTag serialize(Object... args) {
        return this.serializer.apply(args);
    }

    public Object createScreen(Object... args) {
        return this.clientScreenCreator.apply(args);
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

    public static class Builder {

        private Function<Object[], CompoundTag> serializer = null;
        private Function<CompoundTag, Object[]> deserializer = null;
        private Function<Object[], Object> clientScreenCreator = null;
        private BiConsumer<ServerScreen, CompoundTag> serverHandler = null;
        private Function<ServerPlayer, ServerScreen> serverScreenCreator;

        public Builder clientScreenCreator(Function<Object[], Object> clientScreenCreator) {
            this.clientScreenCreator = clientScreenCreator;
            return this;
        }

        public Builder deserializer(Function<CompoundTag, Object[]> deserializer) {
            this.deserializer = deserializer;
            return this;
        }

        public Builder serializer(Function<Object[], CompoundTag> serializer) {
            this.serializer = serializer;
            return this;
        }

        public Builder serverHandler(BiConsumer<ServerScreen, CompoundTag> serverHandler) {
            this.serverHandler = serverHandler;
            return this;
        }

        public Builder serverScreenCreator(Function<ServerPlayer, ServerScreen> serverScreenCreator) {
            this.serverScreenCreator = serverScreenCreator;
            return this;
        }

        @SuppressWarnings("unchecked")
        public <S extends ServerScreen> ScreenBuilder<S> build(String id) {
            if (this.serializer == null) {
                throw new NullPointerException("Serializer must be set");
            }
            if (this.deserializer == null) {
                throw new NullPointerException("Deserializer must be set");
            }
            if (this.clientScreenCreator == null) {
                throw new NullPointerException("Client Screen Creator must be set");
            }
            if (this.serverHandler == null) {
                throw new NullPointerException("Server Handler must be set");
            }
            if (this.serverScreenCreator == null) {
                throw new NullPointerException("Server Screen Creator must be set");
            }
            if (builders.containsKey(id)) {
                throw new NullPointerException("Duplicate screen id %s".formatted(id));
            }
            return new ScreenBuilder<>(id, this.serializer, this.deserializer, this.clientScreenCreator, (BiConsumer<S, CompoundTag>) this.serverHandler, (Function<ServerPlayer, S>) this.serverScreenCreator);
        }

    }

}
