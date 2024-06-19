package net.refractionapi.refraction.interaction;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.refractionapi.refraction.networking.C2S.SyncInteractionC2SPacket;
import net.refractionapi.refraction.networking.RefractionMessages;
import net.refractionapi.refraction.networking.S2C.HandleInteractionS2CPacket;

import java.util.HashMap;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;

public class InteractionBuilder<T extends NPCInteraction> {

    private static final HashMap<String, InteractionBuilder<?>> builders = new HashMap<>();
    private final HashMap<Player, T> interactions = new HashMap<>();
    private final String id;
    private final Class<T> clazz;
    private BiConsumer<T, CompoundTag> handler = (interaction, tag) -> {
    };
    private Consumer<Object[]> clientHandler = (args) -> {
    };
    private Function<Object[], T> constructor = (args) -> null;
    private Function<CompoundTag, Object[]> deserializer = (tag) -> null;
    private Function<Object[], CompoundTag> serializer = (args) -> null;

    public InteractionBuilder(String id, Class<T> clazz) {
        this.id = id;
        this.clazz = clazz;
        builders.put(id, this);
    }

    public InteractionBuilder<T> serverHandler(BiConsumer<T, CompoundTag> context) {
        this.handler = context;
        return this;
    }

    public InteractionBuilder<T> clientHandler(Consumer<Object[]> context) {
        this.clientHandler = context;
        return this;
    }

    public InteractionBuilder<T> clientDeserializer(Function<CompoundTag, Object[]> deserializer) {
        this.deserializer = deserializer;
        return this;
    }

    public InteractionBuilder<T> clientSerializer(Function<Object[], CompoundTag> serializer) {
        this.serializer = serializer;
        return this;
    }

    public InteractionBuilder<T> constructor(Function<Object[], T> constructor) {
        this.constructor = constructor;
        return this;
    }

    public void handleServer(Player player, CompoundTag tag) {
        this.interactions.computeIfPresent(player, (key, value) -> {
            if (value.stillValid() && value.handleSwitch(tag)) {
                this.handler.accept(value, tag);
            } else if (!value.stillValid()) {
                CompoundTag tagTo = new CompoundTag();
                tagTo.putBoolean("close", true);
                RefractionMessages.sendToPlayer(new HandleInteractionS2CPacket(this.getId(), tagTo), (ServerPlayer) player);
                return null;
            }
            return value;
        });
    }

    public void handleClient(CompoundTag tag) {
        this.clientHandler.accept(this.deserializer.apply(tag));
    }

    public void sendToServer(NPCInteraction interaction, CompoundTag tag) {
        interaction.serialize(tag);
        RefractionMessages.sendToServer(new SyncInteractionC2SPacket(this.getId(), tag));
    }

    public T sendInteraction(Player player, Object... args) {
        T interaction = this.constructor.apply(args);
        if (player instanceof ServerPlayer serverPlayer) {
            CompoundTag tag = this.serializer.apply(args);
            RefractionMessages.sendToPlayer(new HandleInteractionS2CPacket(this.getId(), tag), serverPlayer);
            this.interactions.put(player, interaction);
        }
        return interaction;
    }

    public String getId() {
        return this.id;
    }

    public static Optional<InteractionBuilder<?>> getBuilder(String id) {
        return Optional.ofNullable(builders.get(id));
    }

}
