package net.refractionapi.refraction.interaction;

import net.minecraft.nbt.CompoundTag;
import net.minecraftforge.network.NetworkEvent;
import net.refractionapi.refraction.networking.C2S.SyncInteractionC2SPacket;
import net.refractionapi.refraction.networking.RefractionMessages;

import java.util.HashMap;
import java.util.function.BiConsumer;

public class InteractionBuilder<T extends NPCInteraction> {

    private static final HashMap<String, InteractionBuilder<?>> builders = new HashMap<>();
    private final String id;
    private final Class<T> clazz;
    private BiConsumer<CompoundTag, NetworkEvent.Context> handler;

    public InteractionBuilder(String id, Class<T> clazz) {
        this.id = id;
        this.clazz = clazz;
        builders.put(id, this);
    }

    public InteractionBuilder<T> consumer(BiConsumer<CompoundTag, NetworkEvent.Context> context) {
        this.handler = context;
        return this;
    }

    public void handle(CompoundTag tag, NetworkEvent.Context context) {
        this.handler.accept(tag, context);
    }

    public void sendToServer(NPCInteraction interaction, CompoundTag tag) {
        interaction.serialize(tag);
        RefractionMessages.sendToServer(new SyncInteractionC2SPacket(this.getId(), tag));
    }

    public String getId() {
        return this.id;
    }

    public static InteractionBuilder<?> getBuilder(String id) {
        return builders.get(id);
    }

}
