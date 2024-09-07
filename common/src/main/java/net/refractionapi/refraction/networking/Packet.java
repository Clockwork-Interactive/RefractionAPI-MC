package net.refractionapi.refraction.networking;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.refractionapi.refraction.Refraction;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.function.Consumer;

public abstract class Packet implements CustomPacketPayload {

    private static final HashMap<Class<? extends Packet>, Type<Packet>> types = new HashMap<>();
    private static final HashMap<Class<? extends Packet>, StreamCodec<FriendlyByteBuf, ? extends Packet>> codecs = new HashMap<>();

    public Packet() {
    }

    public Packet(FriendlyByteBuf buf) {
    }

    public ResourceLocation getId() {
        return ResourceLocation.tryBuild(Refraction.MOD_ID, this.getClass().getSimpleName().toLowerCase());
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return getPacketType(this.getClass());
    }

    @SuppressWarnings("unchecked")
    public static <P extends Packet> Type<P> getPacketType(Class<? extends Packet> packetClass) {
        return (Type<P>) types.computeIfAbsent(packetClass, (r) -> new Type<>(Refraction.id(packetClass.getSimpleName().toLowerCase()))); // I'M LAZY --Zeus
    }

    @SuppressWarnings("unchecked")
    public static <P extends Packet> StreamCodec<FriendlyByteBuf, P> getCodec(Class<P> msgClass) {
        return (StreamCodec<FriendlyByteBuf, P>) codecs.computeIfAbsent(msgClass, (r) -> StreamCodec.of((buf, packet) -> packet.write(buf), (buf -> {
            P packet = null;
            try {
                packet = msgClass.getConstructor(FriendlyByteBuf.class).newInstance(buf);
            } catch (Exception e) {
                Refraction.LOGGER.error("Couldn't create packet from buf!", e);
            }
            return packet;
        })));
    }

    public abstract void write(FriendlyByteBuf buf);

    public abstract void handle(@Nullable Player player, Consumer<Runnable> context);

}
